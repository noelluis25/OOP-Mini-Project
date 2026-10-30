package com.example;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.activity.ComponentActivity;
import androidx.activity.OnBackPressedCallback;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.data.db.AppDatabase;
import com.example.data.repository.PocketPalRepository;
import com.example.data.storage.JsonStorageManager;
import com.example.ui.components.BottomNavView;
import com.example.ui.components.UiUtils;
import com.example.ui.components.UserSwitcherDialogHelper;
import com.example.ui.screens.AddTransactionView;
import com.example.ui.screens.CreateProfileView;
import com.example.ui.screens.GoalsView;
import com.example.ui.screens.HomeView;
import com.example.ui.screens.MoreProfileView;
import com.example.ui.screens.OnboardingView;
import com.example.ui.screens.SpendingBreakdownView;
import com.example.ui.screens.TransactionsView;
import com.example.ui.viewmodel.AppScreen;
import com.example.ui.viewmodel.DashboardData;
import com.example.ui.viewmodel.PocketPalViewModel;

public class MainActivity extends ComponentActivity implements PocketPalViewModel.StateListener {

    private PocketPalViewModel viewModel;
    private LinearLayout rootShell;
    private FrameLayout contentContainer;
    private FrameLayout bottomNavContainer;
    private boolean userSwitcherShowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        initializeAppAndUi();
    }

    private void initializeAppAndUi() {
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        JsonStorageManager jsonStorageManager = new JsonStorageManager(getApplicationContext());
        PocketPalRepository repository = new PocketPalRepository(
                database.userDao(),
                database.transactionDao(),
                database.goalDao(),
                database.userPreferencesDao(),
                jsonStorageManager
        );

        viewModel = new ViewModelProvider(this, new PocketPalViewModel.Factory(repository))
                .get(PocketPalViewModel.class);

        rootShell = new LinearLayout(this);
        rootShell.setOrientation(LinearLayout.VERTICAL);
        rootShell.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));

        contentContainer = new FrameLayout(this);
        contentContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));
        rootShell.addView(contentContainer);

        bottomNavContainer = new FrameLayout(this);
        bottomNavContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        rootShell.addView(bottomNavContainer);

        ViewCompat.setOnApplyWindowInsetsListener(rootShell, new androidx.core.view.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            }
        });

        setContentView(rootShell);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!viewModel.navigateBack()) {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });

        viewModel.addListener(this);
        renderCurrentState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.removeListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == com.example.ui.components.VoiceConfirmationDialogHelper.REQUEST_RECORD_AUDIO) {
            if (grantResults.length > 0 && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                com.example.ui.components.VoiceConfirmationDialogHelper.startVoiceEntry(this, viewModel);
            } else {
                android.widget.Toast.makeText(this, "Microphone permission is required for voice entry", android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStateChanged() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                renderCurrentState();
            }
        });
    }

    private void renderCurrentState() {
        AppScreen screen = viewModel.getCurrentScreen();
        DashboardData data = viewModel.getDashboardData();
        boolean isDark = data.preferences != null && data.preferences.isDarkMode;

        rootShell.setBackgroundColor(UiUtils.getBgColor(isDark));
        contentContainer.removeAllViews();
        bottomNavContainer.removeAllViews();

        View screenView;
        switch (screen) {
            case ONBOARDING:
                screenView = OnboardingView.create(this, new Runnable() {
                    @Override
                    public void run() {
                        viewModel.completeOnboardingAndProceed();
                    }
                });
                break;
            case CREATE_PROFILE:
                screenView = CreateProfileView.create(this, viewModel);
                break;
            case ADD_TRANSACTION:
                screenView = AddTransactionView.create(this, viewModel);
                break;
            case TRANSACTIONS:
                screenView = TransactionsView.create(this, viewModel);
                break;
            case SPENDING_BREAKDOWN:
                screenView = SpendingBreakdownView.create(this, viewModel);
                break;
            case GOALS:
                screenView = GoalsView.create(this, viewModel);
                break;
            case MORE:
                screenView = MoreProfileView.create(this, viewModel);
                break;
            case HOME:
            default:
                screenView = HomeView.create(this, viewModel);
                break;
        }
        contentContainer.addView(screenView);
        if (UiUtils.areAnimationsEnabled(this)) {
            screenView.setAlpha(0f);
            screenView.setTranslationY(UiUtils.dp(this, 18));
            screenView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(260)
                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                    .start();
        }

        boolean showBottomBar = screen != AppScreen.ONBOARDING
                && screen != AppScreen.CREATE_PROFILE
                && screen != AppScreen.ADD_TRANSACTION;

        if (showBottomBar) {
            bottomNavContainer.setVisibility(View.VISIBLE);
            bottomNavContainer.addView(BottomNavView.create(
                    this,
                    screen,
                    isDark,
                    new BottomNavView.NavListener() {
                        @Override
                        public void onNavigate(AppScreen target) {
                            viewModel.navigateTo(target);
                        }

                        @Override
                        public void onQuickAdd() {
                            viewModel.startAddTransaction();
                        }
                    }
            ));
        } else {
            bottomNavContainer.setVisibility(View.GONE);
        }

        if (viewModel.isShowUserSwitcherDialog() && !userSwitcherShowing) {
            userSwitcherShowing = true;
            UserSwitcherDialogHelper.show(this, viewModel);
        } else if (!viewModel.isShowUserSwitcherDialog()) {
            userSwitcherShowing = false;
        }
    }
}

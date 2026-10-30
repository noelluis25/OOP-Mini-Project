package com.example.ui.components;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import java.util.ArrayList;

public class VoiceInputManager {

    public interface VoiceInputListener {
        void onStatusChanged(String statusMessage);
        void onVolumeChanged(float rmsdB);
        void onSpeechResult(String recognizedText);
        void onError(String errorMessage);
    }

    private final Context context;
    private final VoiceInputListener listener;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;

    public VoiceInputManager(Context context, VoiceInputListener listener) {
        this.context = context.getApplicationContext();
        this.listener = listener;
    }

    public boolean isAvailable() {
        return SpeechRecognizer.isRecognitionAvailable(context);
    }

    public void startListening() {
        stopListening();

        if (!isAvailable()) {
            if (listener != null) {
                listener.onError("Speech recognition is not available on this device.");
            }
            return;
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                isListening = true;
                if (listener != null) {
                    listener.onStatusChanged("Listening... Speak now 🎙️");
                }
            }

            @Override
            public void onBeginningOfSpeech() {
                if (listener != null) {
                    listener.onStatusChanged("Listening...");
                }
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                if (listener != null) {
                    listener.onVolumeChanged(rmsdB);
                }
            }

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {
                isListening = false;
                if (listener != null) {
                    listener.onStatusChanged("Processing speech...");
                }
            }

            @Override
            public void onError(int error) {
                isListening = false;
                String msg = getErrorMessage(error);
                if (listener != null) {
                    listener.onError(msg);
                }
            }

            @Override
            public void onResults(Bundle results) {
                isListening = false;
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String text = matches.get(0);
                    if (listener != null) {
                        listener.onSpeechResult(text);
                    }
                } else {
                    if (listener != null) {
                        listener.onError("Didn't catch that. Please try again.");
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "en-IN");
        intent.putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, false);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something like 'Spent 50 rupees on snacks'...");

        try {
            speechRecognizer.startListening(intent);
        } catch (Exception e) {
            isListening = false;
            if (listener != null) {
                listener.onError("Unable to start microphone: " + e.getMessage());
            }
        }
    }

    public void stopListening() {
        if (speechRecognizer != null) {
            try {
                speechRecognizer.stopListening();
                speechRecognizer.destroy();
            } catch (Exception ignored) {
            }
            speechRecognizer = null;
        }
        isListening = false;
    }

    public boolean isListening() {
        return isListening;
    }

    private String getErrorMessage(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error. Check microphone.";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client side error. Please try again.";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Microphone permission is required for voice entry.";
            case SpeechRecognizer.ERROR_NETWORK:
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network connection error. Check internet connection.";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No speech heard. Please speak clearly.";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Voice recognizer is busy. Please try again.";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech detected. Tap mic to try again.";
            default:
                return "Voice recognition error (" + error + "). Please try again.";
        }
    }
}

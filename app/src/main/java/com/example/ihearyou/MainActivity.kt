package com.example.IHearYou

import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.ihearyou.ui.theme.IHearYouTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set the Jetpack Compose UI as the content view
        setContent {
            IHearYouTheme {
                IHearYouApp() // Launch the main composable
            }
        }
    }
}

@Composable
fun IHearYouApp() {
    // State to hold the current screen color
    var screenColor by remember { mutableStateOf(Color.White) }
    // State to hold the recognized text or instructions
    var recognizedText by remember { mutableStateOf("Say 'Blue' or 'Red'") }
    // Get the current context for speech recognition and text-to-speech
    val context = LocalContext.current

    // Initialize SpeechRecognizer
    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context).apply {
            // Set up a listener to handle speech recognition events
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    // Called when the system is ready to accept speech input
                }


                override fun onBeginningOfSpeech() {
                    // Called when the user starts speaking
                }

                override fun onRmsChanged(rmsdB: Float) {
                    // Called when the sound level in the audio stream changes
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // Called when partial recognition results are available
                }

                override fun onEndOfSpeech() {
                    // Called when the user stops speaking
                }

                override fun onError(error: Int) {
                    // Handle errors during speech recognition
                    recognizedText = "Error: ${getErrorText(error)}"
                }

                override fun onResults(results: Bundle?) {
                    // Called when final recognition results are available
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        // Get the first recognized word and convert it to lowercase
                        val recognizedWord = matches[0].lowercase()
                        recognizedText = "You said: $recognizedWord"
                        // Update the screen color and speak the response based on the recognized word
                        when (recognizedWord) {
                            "blue" -> {
                                screenColor = Color.Blue
                                context.speak("Here is the blue screen")
                            }
                            "red" -> {
                                screenColor = Color.Red
                                context.speak("Here is the red screen")
                            }
                            else -> {
                                screenColor = Color.White
                                context.speak("I didn't understand")
                            }
                        }
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    // Called when partial recognition results are available
                }

                override fun onEvent(eventType: Int, params: Bundle?) {
                    // Called for other speech recognition events
                }
            })
        }
    }

    // Start listening for speech input when the composable is launched
    LaunchedEffect(Unit) {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            // Configure the speech recognition settings
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        }
        speechRecognizer.startListening(intent) // Start listening
    }

    // UI for the app
    Box(
        modifier = Modifier
            .fillMaxSize() // Fill the entire screen
            .background(screenColor), // Set the background color based on the state
        contentAlignment = Alignment.Center // Center the text
    ) {
        Text(
            text = recognizedText, // Display the recognized text or instructions
            fontSize = 24.sp,
            color = Color.White, // Ensure the text is visible on colored backgrounds
            textAlign = TextAlign.Center
        )
    }
}

// Extension function to speak text using Android's text-to-speech capabilities
fun android.content.Context.speak(text: String) {
    val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
        putExtra(RecognizerIntent.EXTRA_PROMPT, text) // Set the text to be spoken
    }
    startActivity(speechIntent) // Start the text-to-speech activity
}

// Helper function to convert speech recognition error codes to human-readable text
fun getErrorText(errorCode: Int): String {
    return when (errorCode) {
        SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
        SpeechRecognizer.ERROR_CLIENT -> "Client side error"
        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
        SpeechRecognizer.ERROR_NETWORK -> "Network error"
        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout"
        SpeechRecognizer.ERROR_NO_MATCH -> "No match"
        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RecognitionService busy"
        SpeechRecognizer.ERROR_SERVER -> "Server error"
        SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech input"
        else -> "Unknown error"
    }
}
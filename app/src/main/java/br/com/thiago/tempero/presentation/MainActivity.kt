package br.com.thiago.tempero.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.thiago.tempero.presentation.constants.TemperoTheme
import br.com.thiago.tempero.presentation.navigation.TemperoNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TemperoTheme {
                TemperoNavHost()
            }
        }
    }
}

package com.example.linphoneapp.Screen

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.linphoneapp.R
import com.example.linphoneapp.viewmodel.LoginViewModel
import com.example.linphoneapp.viewmodel.SplashViewModel
import kotlinx.coroutines.delay
import org.linphone.core.RegistrationState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: SplashViewModel = hiltViewModel()
    val loginViewModel: LoginViewModel = hiltViewModel()

    val reg by viewModel.regState.collectAsState()
    
    var logoScale by remember { mutableStateOf(0f) }
    var logoRotation by remember { mutableStateOf(0f) }
    var textAlpha by remember { mutableStateOf(0f) }
    var progressAlpha by remember { mutableStateOf(0f) }
    val prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE)


    val username = prefs.getString("username","")
    val password = prefs.getString("password","")

    LaunchedEffect(Unit) {
        // Logo entrance with bounce
        animate(0f, 1.2f, animationSpec = tween(800, easing = FastOutSlowInEasing)) { value, _ ->
            logoScale = value
        }
        animate(1.2f, 1f, animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        )) { value, _ ->
            logoScale = value
        }

        delay(200)

        // Logo rotation effect
        animate(0f, 360f, animationSpec = tween(1000, easing = LinearEasing)) { value, _ ->
            logoRotation = value
        }

        delay(200)

        // Text fade in
        animate(0f, 1f, animationSpec = tween(600)) { value, _ ->
            textAlpha = value
        }

        delay(400)

        // Progress indicator
        animate(0f, 1f, animationSpec = tween(400)) { value, _ ->
            progressAlpha = value
        }

        delay(1000)

        if(viewModel.isLoggedIn(context)){

            Log.d("TAG>>", "SplashScreen: loggedi"+reg)

            if(reg != RegistrationState.Ok){
                loginViewModel.register(
                    username!!,password!!
                )

            }
            onNavigateToHome()
        }else{
            Log.d("TAG>>", "SplashScreen not loggedin")

            onNavigateToLogin()
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF667eea),
                        Color(0xFF764ba2),
                        Color(0xFF4c63d2)
                    ),
                    radius = 1000f
                )
            )
    ) {

        // Animated background elements
        repeat(7) { index ->
            val offsetX = remember { (-200..400).random().dp }
            val offsetY = remember { (-100..800).random().dp }
            val size = remember { (80..150).random().dp }

            var animatedOffset by remember { mutableStateOf(0f) }

            LaunchedEffect(Unit) {
                animate(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween((3000..6000).random(), easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                ) { value, _ ->
                    animatedOffset = value
                }
            }

            Box(
                modifier = Modifier
                    .size(size)
                    .offset(
                        x = offsetX + (animatedOffset * 50).dp,
                        y = offsetY + (animatedOffset * 30).dp
                    )
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f + animatedOffset * 0.05f))
                    .blur(20.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Container
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .rotate(logoRotation),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            )
                        )
                )

                // Inner logo
                Card(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(    painter = painterResource(id = R.drawable.user)
                            ,"", Modifier
                                .size(40.dp)
                                .alpha(0.8f))

                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = TextStyle(
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 6.sp
                ),
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Connect • Communicate • Collaborate",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Loading indicator
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.alpha(progressAlpha)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(200.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.Version_name),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                )
            }
        }

        // Version info
//        Text(
//            text = "v1.0.0",
//            style = TextStyle(
//                fontSize = 12.sp,
//                color = Color.White.copy(alpha = 0.5f)
//            ),
//            modifier = Modifier
//                .padding(bottom = 32.dp)
//                .alpha(textAlpha)
//        )
    }


}

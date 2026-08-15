package dev.elelan.quotequiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Tray
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberNotification
import androidx.compose.ui.window.rememberTrayState
import androidx.compose.ui.window.rememberWindowState
import java.awt.Desktop
import java.io.InputStream
import java.net.URI
import javax.imageio.ImageIO

fun main() = application {

    // Load window icon from resources
    val iconStream: InputStream? =
        Thread.currentThread().contextClassLoader.getResourceAsStream("AppIcon512.png")
    val windowIcon = iconStream?.use { stream ->
        BitmapPainter(ImageIO.read(stream).toComposeImageBitmap())
    }

    val windowState = rememberWindowState(
        position = WindowPosition(Alignment.Center),
        size = DpSize(1024.dp, 768.dp)
    )

    var showAboutDialog by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Wisdom Trivia",
        icon = windowIcon,
        state = windowState
    ) {
        val trayState = rememberTrayState()
        val notification = rememberNotification("Quiz Ready!", "Your daily quiz is generated.")

        Tray(
            icon = painterResource("AppIcon512.png"),
            state = trayState,
            tooltip = "QuoteQuiz",
            menu = {
                Item("Show Window", onClick = { /* bring window to front */ })
                Item(
                    "Send Test Notification",
                    onClick = { trayState.sendNotification(notification) })
                Item("Exit", onClick = ::exitApplication)
            }
        )

        // Desktop Application Top Menu Bar
        MenuBar {
            Menu("File", mnemonic = 'F') {
                Item(
                    text = "About Wisdom Trivia",
                    shortcut = KeyShortcut(Key.I, ctrl = true),
                    onClick = { showAboutDialog = true }
                )
                Separator()
                Item(
                    text = "Exit",
                    shortcut = KeyShortcut(Key.Q, ctrl = true),
                    onClick = ::exitApplication
                )
            }
        }

        App()

        // Desktop Native About Dialog
        if (showAboutDialog) {
            DialogWindow(
                onCloseRequest = { showAboutDialog = false },
                state = rememberDialogState(
                    position = WindowPosition(Alignment.Center),
                    size = DpSize(420.dp, 320.dp)
                ),
                title = "About Wisdom Trivia",
                resizable = false,
                icon = windowIcon
            ) {
                MaterialTheme {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Wisdom Trivia",
                            style = MaterialTheme.typography.h5
                        )
                        Text(
                            text = "Version 1.0.0",
                            style = MaterialTheme.typography.caption,
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "A full-stack Kotlin Multiplatform quote quiz application powered by Compose Multiplatform and Spring Boot backend.",
                            style = MaterialTheme.typography.body2
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (Desktop.isDesktopSupported() && Desktop.getDesktop()
                                        .isSupported(Desktop.Action.BROWSE)
                                ) {
                                    Desktop.getDesktop()
                                        .browse(URI("https://evic2132.github.io/quote-quiz/"))
                                }
                            }
                        ) {
                            Text("Open Live Web App")
                        }
                    }
                }
            }
        }
    }
}

package com.techtactoe.mention

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.techtactoe.mention.screens.detail.DetailScreen
import com.techtactoe.mention.screens.list.ListScreen
import com.techtactoe.mention.ui.screens.login.LoginScreen
import com.techtactoe.mention.ui.theme.MentionTheme
import kotlinx.serialization.Serializable

@Serializable
object LoginDestination

@Serializable
object ListDestination

@Serializable
data class DetailDestination(val objectId: Int)

@Composable
fun App() {
    MentionTheme {
        Surface {
            val navController: NavHostController = rememberNavController()
            NavHost(navController = navController, startDestination = LoginDestination) {
                composable<LoginDestination> {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(ListDestination)
                        }
                    )
                }
                composable<ListDestination> {
                    ListScreen(navigateToDetails = { objectId ->
                        navController.navigate(DetailDestination(objectId))
                    })
                }
                composable<DetailDestination> { backStackEntry ->
                    DetailScreen(
                        objectId = backStackEntry.toRoute<DetailDestination>().objectId,
                        navigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}

package com.padgett.progressnotes

import androidx.navigation.NavHostController

object ProgressRouteNames {
    const val SIGN_IN_ROUTE = "sign_in_route"
    const val HOME_ROUTE = "home_route"
}

object ProgressDestinations {
    const val SIGN_IN_DESTINATION = ProgressRouteNames.SIGN_IN_ROUTE
    const val HOME_DESTINATION = ProgressRouteNames.HOME_ROUTE
}

class ProgressNavigationActions(private val navController: NavHostController) {
    fun navigateToSignIn() {
        navController.navigate(ProgressDestinations.SIGN_IN_DESTINATION)
    }

    fun navigateToHome() {
        navController.navigate(ProgressDestinations.HOME_DESTINATION)
    }
}
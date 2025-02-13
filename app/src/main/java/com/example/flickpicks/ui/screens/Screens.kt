package com.example.flickpicks.ui.screens

sealed class Screens (val screen: String){
    data object MyFeed: Screens(screen = "myfeed")
    data object Party: Screens(screen = "party")
    data object Search : Screens(screen = "search")
    data object Friends: Screens(screen = "friends")
    data object Profile: Screens(screen = "profile")
    data object EditProfile: Screens(screen = "editProfile")
    data object Settings: Screens(screen = "settings")
    data object Entry : Screens(screen = "Entry")
    data object SignUp : Screens(screen = "signUp")
    data object SignIn : Screens(screen = "signIn")
    data object PartyGroup: Screens(screen = "partyGroup")
    data object MovieDetail : Screens(screen = "movieDetail/{movieId}") {
        fun createRoute(movieId: Int) = "movieDetail/$movieId"
    }
}
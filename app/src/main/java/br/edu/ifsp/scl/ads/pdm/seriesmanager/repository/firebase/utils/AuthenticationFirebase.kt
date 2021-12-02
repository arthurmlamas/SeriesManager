package br.edu.ifsp.scl.ads.pdm.seriesmanager.repository.firebase.utils

import com.google.firebase.auth.FirebaseAuth

object AuthenticationFirebase {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
}
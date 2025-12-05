package dev.luisbaena.prodentclient

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*
    *  Clase Application para Hilt
    *  Este archivo sirve para que Hilt pueda inyectar dependencias
    *  automáticamente en toda la app desde el inicio.
 */
@HiltAndroidApp
class ProdentApplication : Application() {
}
package com.kapoue.opecours.domain.model

import androidx.compose.ui.graphics.Color
import com.kapoue.opecours.R

enum class Operator(
    val displayName: String,
    val symbol: String,
    val colorPrimary: Color,
    val logoRes: Int,
    val isActive: Boolean = true
) {
    ORANGE("Orange", "ORA.PA", Color(0xFFFF7900), R.drawable.ic_orange, true),
    BOUYGUES("Bouygues", "EN.PA", Color(0xFF005FAF), R.drawable.ic_bouygues, true),
    SFR("SFR", "ATC.PA", Color(0xFFE50000), R.drawable.ic_sfr, false), // Pas de données disponibles
    FREE("Free", "ILD.PA", Color(0xFFCD1E25), R.drawable.ic_free, false) // Données obsolètes depuis 2021
}
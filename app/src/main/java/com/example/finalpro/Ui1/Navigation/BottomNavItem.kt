package com.example.finalpro.Ui1.Navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Login      : Screen("login")
    object Register   : Screen("register")
    object Dashboard  : Screen("dashboard")
    object Gastos     : Screen("gastos")
    object Ingresos   : Screen("ingresos")
    object Presupuesto: Screen("presupuesto")
    object Metas      : Screen("metas")
    object Reportes   : Screen("reportes")
}

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem("Inicio",      Screen.Dashboard.route,   Icons.Rounded.Home),
    BottomNavItem("Gastos",      Screen.Gastos.route,      Icons.Rounded.ArrowDownward),
    BottomNavItem("Ingresos",    Screen.Ingresos.route,    Icons.Rounded.ArrowUpward),
    BottomNavItem("Presupuesto", Screen.Presupuesto.route, Icons.Rounded.PieChart),
    BottomNavItem("Metas",       Screen.Metas.route,       Icons.Rounded.EmojiEvents),
)
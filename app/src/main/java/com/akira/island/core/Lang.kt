package com.akira.island.core

import android.content.res.Resources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.platform.LocalConfiguration
import com.akira.island.data.AppLanguage
import com.akira.island.data.SettingsRepo
import java.util.Locale

/**
 * Idioma do app e da ilha: russo ou português (Brasil). «Como no sistema» — russo se o telefone estiver em russo,
 * senão português. Os textos são escritos em pares direto no código: tr("Зарядка", "Carregando").
 */
object Lang {
    /** true — textos em português (Brasil); false — em russo. */
    val pt: Boolean
        get() = when (SettingsRepo.current.language) {
            AppLanguage.PT -> true
            AppLanguage.RU -> false
            AppLanguage.SYSTEM -> systemLanguage() != "ru"
        }

    /** Para datas: «segunda-feira, 9 de novembro» ou «понедельник, 9 ноября» */
    val locale: Locale get() = if (pt) Locale.forLanguageTag("pt-BR") else Locale.forLanguageTag("ru")

    private fun systemLanguage(): String =
        runCatching { Resources.getSystem().configuration.locales[0].language }.getOrNull() ?: Locale.getDefault().language
}

/** Texto no idioma atual. */
fun tr(ru: String, pt: String): String = if (Lang.pt) pt else ru

/**
 * Trocou o idioma (nas configurações ou no sistema) — tudo por dentro é redesenhado já no novo idioma.
 * O estado de fora (aba e tela abertas) continua.
 */
@Composable
fun LangScope(content: @Composable () -> Unit) {
    val s by SettingsRepo.state.collectAsState()
    // O idioma do sistema muda junto com a configuração — ler isso nos inscreve nela
    LocalConfiguration.current
    key(s.language, Lang.pt) { content() }
}

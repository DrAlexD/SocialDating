package xelagurd.socialdating.ui.screen

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import xelagurd.socialdating.R

@Composable
@ReadOnlyComposable
fun stringResourceWithColon(@StringRes id: Int) =
    stringResource(R.string.text_with_colon, stringResource(id))
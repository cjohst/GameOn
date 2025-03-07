package com.example.gameon.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.gameon.R
import com.example.gameon.ui.theme.BlueDark
import com.example.gameon.ui.theme.BlueDarker
import com.example.gameon.ui.theme.BlueLight
import com.example.gameon.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownInput(
    label: String,
    options: List<T>,
    selectedOption: MutableState<T?>,
    modifier: Modifier,
    displayText: (T) -> String,
    leadingIcon: ((T) -> @Composable (() -> Unit)?)? = null,
    onSelect: () -> Unit = { }
) {
    var expanded by remember { mutableStateOf(false) }
    val fontFamily = FontFamily(Font(R.font.lato_regular))

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            readOnly = true,
            value = selectedOption.value?.let { displayText(it) } ?: "",
            onValueChange = { },
            label = { Text(label, fontFamily = fontFamily) },
            singleLine = false,
            textStyle = TextStyle(fontFamily = fontFamily),
            colors = TextFieldDefaults.colors(
                unfocusedTextColor = White,
                unfocusedLabelColor = White,
                unfocusedTrailingIconColor = White,
                unfocusedContainerColor = BlueDark,
                unfocusedIndicatorColor = BlueDark,
                unfocusedPlaceholderColor = Color(0xAAFFFFFF),
                focusedTextColor = White,
                focusedLabelColor = White,
                focusedTrailingIconColor = White,
                focusedContainerColor = BlueDark,
                focusedIndicatorColor = White,
                focusedPlaceholderColor = Color(0xCCFFFFFF),
            ),
            leadingIcon = selectedOption.value?.let { leadingIcon?.invoke(it) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = modifier
                .background(BlueDark)
                .padding(horizontal = 2.dp)
                .heightIn(max = 250.dp)
        ) {
            options.forEach { option ->
                val optionText = displayText(option)
                val selectedText = selectedOption.value?.let { displayText(it) }

                val textColor = if (selectedText == optionText) BlueLight else White
                val bgColor = if (selectedText == optionText) BlueDarker else BlueDark

                DropdownMenuItem(
                    text = { Text(optionText) },
                    onClick = {
                        selectedOption.value = option
                        onSelect()
                        expanded = false
                    },
                    colors = MenuItemColors(
                        textColor = textColor,
                        leadingIconColor = Color.Transparent,
                        trailingIconColor = Color.Transparent,
                        disabledTextColor = Color.Transparent,
                        disabledLeadingIconColor = Color.Transparent,
                        disabledTrailingIconColor = Color.Transparent
                    ),
                    leadingIcon = leadingIcon?.invoke(option),
                    modifier = Modifier.background(bgColor)
                )
            }
        }
    }
}
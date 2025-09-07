package com.example.audio_player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Settings(navController: NavController, viewModel: PlayerViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(start = 10.dp),
        contentPadding = PaddingValues(bottom = 55.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .clickable(
                        onClick = {
                            navController.navigate("theme_change")
                        }
                    ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painterResource(R.drawable.color_pallette),
                    contentDescription = "Change theme",
                    tint = viewModel.iconColor
                )
                LcdText(
                    "Theme",
                    viewModel = viewModel
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeChange(viewModel: PlayerViewModel) {
    val colourList = listOf("Default", "Red", "Green", "Blue", "Light blue", "Yellow", "Orange", "Black", "White", "Pink", "Purple", "Cyan", "Magenta")
    val colourOtherList = listOf("Default", "Red", "Green", "Blue","Light blue", "Yellow", "Orange", "Black", "Pink", "Purple", "Cyan", "Magenta")
    @Composable
    fun ColourListDropDownMenu(name: String, choice: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            var expanded by remember { mutableStateOf(false) }
            var selectedText by remember { mutableStateOf(colourOtherList[0]) }
            LcdText(
                "Change $name colour: ",
                viewModel = viewModel
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    colourList.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = {
                                LcdText(text, viewModel = viewModel)
                            },
                            onClick = {
                                selectedText = colourOtherList[index]
                                viewModel.updateColor(choice, colourList[index])
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
    @Composable
    fun ColourOtherListDropDownMenu(name: String, choice: String) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            var expanded by remember { mutableStateOf(false) }
            var selectedText by remember { mutableStateOf(colourOtherList[0]) }
            LcdText(
                "Change $name colour: ",
                viewModel = viewModel
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    value = selectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    colourOtherList.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = {
                                LcdText(text, viewModel = viewModel)
                            },
                            onClick = {
                                selectedText = colourOtherList[index]
                                viewModel.updateColor(choice, colourOtherList[index])
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(
                top = 10.dp,
                start = 10.dp
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
        ) {
        ColourListDropDownMenu("primary","primary")
        ColourListDropDownMenu("secondary","secondary")
        ColourListDropDownMenu("tertiary","tertiary")
        ColourListDropDownMenu("background","background")
        ColourOtherListDropDownMenu("text","text")
        ColourOtherListDropDownMenu("icon","icon")
        ColourOtherListDropDownMenu("equaliser's level","eqLevel")
        ColourListDropDownMenu("equaliser's text","eqText")
        ColourListDropDownMenu("seek bar's thumb","sliderThumb")
        ColourListDropDownMenu("seek bar's track","sliderTrack")
    }
}
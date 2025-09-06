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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    tint = Color.White
                )
                LcdText(
                    "Theme"
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeChange(viewModel: PlayerViewModel) {
    val colourList = listOf("Default", "Red", "Green", "Blue", "Yellow", "Orange", "Black", "White", "Pink", "Purple", "Cyan", "Magenta")
    var primaryExpanded by remember { mutableStateOf(false) }
    var secondaryExpanded by remember { mutableStateOf(false) }
    var tertiaryExpanded by remember { mutableStateOf(false) }
    var backgroundExpanded by remember { mutableStateOf(false) }
    var primarySelectedText by remember { mutableStateOf(colourList[0]) }
    var secondarySelectedText by remember { mutableStateOf(colourList[0]) }
    var tertiarySelectedText by remember { mutableStateOf(colourList[0]) }
    var backgroundSelectedText by remember { mutableStateOf(colourList[0]) }

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
        Row( // Primary colour
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            LcdText(
                "Change primary colour: "
            )
            ExposedDropdownMenuBox(
                expanded = primaryExpanded,
                onExpandedChange = { primaryExpanded = !primaryExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    value = primarySelectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = primaryExpanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = primaryExpanded,
                    onDismissRequest = { primaryExpanded = false }
                ) {
                    colourList.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = {
                                LcdText(text)
                            },
                            onClick = {
                                primarySelectedText = colourList[index]
                                viewModel.updateColor("primary", colourList[index])
                                primaryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
        Row( // Secondary colour
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            LcdText(
                "Change secondary colour: "
            )
            ExposedDropdownMenuBox(
                expanded = secondaryExpanded,
                onExpandedChange = { secondaryExpanded = !secondaryExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    value = secondarySelectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = secondaryExpanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = secondaryExpanded,
                    onDismissRequest = { secondaryExpanded = false }
                ) {
                    colourList.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = {
                                LcdText(text)
                            },
                            onClick = {
                                secondarySelectedText = colourList[index]
                                viewModel.updateColor("secondary", colourList[index])
                                secondaryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
        Row( // Tertiary colour
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            LcdText(
                "Change tertiary colour: "
            )
            ExposedDropdownMenuBox(
                expanded = tertiaryExpanded,
                onExpandedChange = { tertiaryExpanded = !tertiaryExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    value = tertiarySelectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = tertiaryExpanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = tertiaryExpanded,
                    onDismissRequest = { tertiaryExpanded = false }
                ) {
                    colourList.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = {
                                LcdText(text)
                            },
                            onClick = {
                                tertiarySelectedText = colourList[index]
                                viewModel.updateColor("tertiary", colourList[index])
                                tertiaryExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
        Row( // Background colour
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(vertical = 5.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            LcdText(
                "Change background colour: "
            )
            ExposedDropdownMenuBox(
                expanded = backgroundExpanded,
                onExpandedChange = { backgroundExpanded = !backgroundExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(
                            type = MenuAnchorType.PrimaryNotEditable,
                            enabled = true
                        ),
                    value = backgroundSelectedText,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = backgroundExpanded
                        )
                    }
                )
                ExposedDropdownMenu(
                    expanded = backgroundExpanded,
                    onDismissRequest = { backgroundExpanded = false }
                ) {
                    colourList.forEachIndexed { index, text ->
                        DropdownMenuItem(
                            text = {
                                LcdText(text)
                            },
                            onClick = {
                                backgroundSelectedText = colourList[index]
                                viewModel.updateColor("background", colourList[index])
                                backgroundExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }
}
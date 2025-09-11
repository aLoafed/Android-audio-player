package com.example.audio_player

import android.content.Context
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.audio_player.ui.theme.LcdGrey
import com.example.audio_player.ui.theme.LightLcdGrey
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlinx.coroutines.launch

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
fun ThemeChange(viewModel: PlayerViewModel, navController: NavController, context: Context) {
    val settingsData = SettingsData(context, context.dataStore)
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
            var selectedText by remember { mutableStateOf("Default") }
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
                    for (i in viewModel.colorMap.keys) {
                        DropdownMenuItem(
                            text = {
                                LcdText(i, viewModel = viewModel)
                            },
                            onClick = {
                                selectedText = i
                                viewModel.updateColor(choice, viewModel.colorMap[i])
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
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
            var selectedText by remember { mutableStateOf("Default") }
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
                    for (i in viewModel.otherColorMap.keys) {
                        DropdownMenuItem(
                            text = {
                                LcdText(i, viewModel = viewModel)
                            },
                            onClick = {
                                selectedText = i
                                viewModel.updateColor(choice, viewModel.otherColorMap[i])
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
//                    for (i in settingsData.preferencesList) {
//                        DropdownMenuItem(
//                            text = {
//                                LcdText(i, viewModel = viewModel)
//                            },
//                            onClick = {
//                                selectedText = i
//                                viewModel.updateColor(choice, viewModel.otherColorMap[i])
//                                expanded = false
//                            },
//                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
//                        )
//                    }
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
        ColourListDropDownMenu("background", "background")
        ColourOtherListDropDownMenu("text", "text")
        ColourOtherListDropDownMenu("icon", "icon")
        ColourOtherListDropDownMenu("equaliser's level", "eqLevel")
        ColourListDropDownMenu("equaliser's text", "eqText")
        ColourListDropDownMenu("seek bar's thumb", "sliderThumb")
        ColourListDropDownMenu("seek bar's track", "sliderTrack")
        Button(
            modifier = Modifier,
            onClick = {
                navController.navigate("color_picker")
            },
            colors = ButtonColors(
                containerColor = LightLcdGrey,
                contentColor = Color.White,
                disabledContainerColor = LightLcdGrey,
                disabledContentColor = Color.White
            )
        ) {
            LcdText(
                "Add a custom color",
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun ColorPicker(viewModel: PlayerViewModel, navController: NavController, context: Context) {
    val settingsData = SettingsData(context, context.dataStore)
    val controller = rememberColorPickerController()
    var selectedColor = Color.White
    val coroutine = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .background(LcdGrey),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AlphaTile(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(6.dp)),
                selectedColor = selectedColor,
                controller = controller
            )
        }
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(10.dp),
            controller = controller,
            onColorChanged = {
                selectedColor = it.color
            }
        )
        AlphaSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
        )
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .height(35.dp),
            controller = controller,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var customColorName = ""
            TextField(
                value = customColorName,
                onValueChange = {
                    customColorName = it
                },
                modifier = Modifier,
                placeholder = {
                    LcdText(
                        "Custom color's name",
                        viewModel = viewModel
                    )
                },

            )
            Button(
                modifier = Modifier,
                onClick = {
                    var tmpName = customColorName
                    if (tmpName == "") {
                        tmpName = "Custom"
                    }
                    var i = 1
                    while (tmpName in viewModel.colorMap) {
                        tmpName = "$tmpName($i)"
                        i ++
                    }
                    viewModel.updateCustomColors(selectedColor, tmpName)
                    coroutine.launch{ settingsData.addCustomColor(tmpName, selectedColor) }
                    navController.navigate("theme_change")
                },
                colors = ButtonColors(
                    containerColor = LightLcdGrey,
                    contentColor = Color.White,
                    disabledContainerColor = LightLcdGrey,
                    disabledContentColor = Color.White,
                )
            ) {
                LcdText(
                    "Apply",
                    viewModel = viewModel
                )
            }
        }
    }
}
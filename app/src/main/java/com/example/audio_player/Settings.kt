package com.example.audio_player

import android.content.Context
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.audio_player.ui.theme.LcdGrey
import com.example.audio_player.ui.theme.LightLcdGrey
import com.example.audio_player.ui.theme.lcdFont
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun Settings(navController: NavController, viewModel: PlayerViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.displayCutout),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
                .background(viewModel.backgroundColor),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = "Back arrow"
                    )
                },
                onClick = {
                    navController.popBackStack()
                },
                colors = IconButtonColors(
                    contentColor = viewModel.iconColor,
                    containerColor = Color.Transparent,
                    disabledContentColor = viewModel.iconColor,
                    disabledContainerColor = Color.Transparent
                )
            )
            Spacer(
                modifier = Modifier
                    .width(5.dp)
            )
            LargeLcdText("Settings", viewModel = viewModel)
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(viewModel.backgroundColor)
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
                        contentDescription = "Customisation",
                        tint = viewModel.iconColor
                    )
                    LcdText(
                        "Customisation",
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorizontalThemeChange(viewModel: PlayerViewModel, navController: NavController, context: Context) {
    val scrollState = ScrollState(0)
    val tmpColorSettings = mutableMapOf<String, Int>()
    val tmpMiscSettings = mutableMapOf(
        "showBasicLoadingScreen" to viewModel.showBasicLoadingScreen,
        "showEqualiser" to viewModel.showEqualiser
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.displayCutout)
            .padding(horizontal = 5.dp)
            .verticalScroll(
                state = scrollState
            ),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Back arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(43.dp)
                .background(viewModel.backgroundColor),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = "Back arrow"
                    )
                },
                onClick = {
                    navController.popBackStack()
                },
                colors = IconButtonColors(
                    contentColor = viewModel.iconColor,
                    containerColor = Color.Transparent,
                    disabledContentColor = viewModel.iconColor,
                    disabledContainerColor = Color.Transparent
                )
            )
            Spacer(
                modifier = Modifier
                    .width(5.dp)
            )
            LargeLcdText("Theme change", viewModel = viewModel)
        }
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
        // To choose the colors for UI
        ColourListDropDownMenu("background", "background", viewModel, tmpColorSettings, viewModel.backgroundColor)
        ColourOtherListDropDownMenu("text", "text", viewModel, tmpColorSettings, viewModel.textColor)
        ColourOtherListDropDownMenu("icon", "icon", viewModel, tmpColorSettings, viewModel.iconColor)
        ColourOtherListDropDownMenu("equaliser's level","eqLevel", viewModel, tmpColorSettings, viewModel.eqLevelColor)
        ColourListDropDownMenu("equaliser's text","eqText", viewModel, tmpColorSettings, viewModel.eqTextColor)
        ColourListDropDownMenu("seek bar's thumb","sliderThumb", viewModel, tmpColorSettings, viewModel.sliderThumbColor)
        ColourListDropDownMenu("seek bar's track","sliderTrack", viewModel, tmpColorSettings, viewModel.sliderTrackColor)
        LoadingScreenTypeSwitch(viewModel, tmpMiscSettings)
        EQVisibilitySwitch(viewModel, tmpMiscSettings)
        // Customisation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomColorButton(viewModel, navController)
            ResetToDefaultsButton(viewModel, context)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            SaveChangesButton(tmpColorSettings, tmpMiscSettings,context, viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortraitThemeChange(viewModel: PlayerViewModel, navController: NavController, context: Context) {
    val tmpColorSettings = mutableMapOf<String, Int>()
    val tmpMiscSettings = mutableMapOf(
        "showBasicLoadingScreen" to viewModel.showBasicLoadingScreen,
        "showEqualiser" to viewModel.showEqualiser
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 5.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Back arrow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(43.dp)
                .background(viewModel.backgroundColor),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = "Back arrow"
                    )
                },
                onClick = {
                    navController.popBackStack()
                },
                colors = IconButtonColors(
                    contentColor = viewModel.iconColor,
                    containerColor = Color.Transparent,
                    disabledContentColor = viewModel.iconColor,
                    disabledContainerColor = Color.Transparent
                )
            )
            Spacer(
                modifier = Modifier
                    .width(5.dp)
            )
            LargeLcdText("Theme change", viewModel = viewModel)
        }
        Spacer(
            modifier = Modifier
                .height(10.dp)
        )
        // To choose the colors for UI
        ColourListDropDownMenu("background", "background", viewModel, tmpColorSettings, viewModel.backgroundColor)
        ColourOtherListDropDownMenu("text", "text", viewModel, tmpColorSettings, viewModel.textColor)
        ColourOtherListDropDownMenu("icon", "icon", viewModel, tmpColorSettings, viewModel.iconColor)
        ColourOtherListDropDownMenu("equaliser's level","eqLevel", viewModel, tmpColorSettings, viewModel.eqLevelColor)
        ColourListDropDownMenu("equaliser's text","eqText", viewModel, tmpColorSettings, viewModel.eqTextColor)
        ColourListDropDownMenu("seek bar's thumb","sliderThumb", viewModel, tmpColorSettings, viewModel.sliderThumbColor)
        ColourListDropDownMenu("seek bar's track","sliderTrack", viewModel, tmpColorSettings, viewModel.sliderTrackColor)
        LoadingScreenTypeSwitch(viewModel, tmpMiscSettings)
        EQVisibilitySwitch(viewModel, tmpMiscSettings)
        // Customisation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CustomColorButton(viewModel, navController)
            ResetToDefaultsButton(viewModel, context)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            SaveChangesButton(tmpColorSettings, tmpMiscSettings,context, viewModel)
        }
    }
}
@Composable
fun EQVisibilitySwitch(viewModel: PlayerViewModel, tmpMiscSettings: MutableMap<String, Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var switched by remember { mutableStateOf(viewModel.showEqualiser) }
        LcdText(
            "Switch equaliser on / off",
            viewModel = viewModel
        )
        Switch(
            checked = switched,
            onCheckedChange = {
                switched = !switched
                tmpMiscSettings["showEqualiser"] = switched
            },
            colors = SwitchColors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LightLcdGrey,
                checkedBorderColor = Color.White,
                checkedIconColor = viewModel.iconColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = LightLcdGrey,
                uncheckedBorderColor = Color.White,
                uncheckedIconColor = viewModel.iconColor,
                disabledCheckedThumbColor = Color.White,
                disabledCheckedTrackColor = Color.White,
                disabledCheckedBorderColor = Color.White,
                disabledCheckedIconColor = Color.White,
                disabledUncheckedThumbColor = Color.White,
                disabledUncheckedTrackColor = Color.White,
                disabledUncheckedBorderColor = Color.White,
                disabledUncheckedIconColor = Color.White
            )
        )
    }
}

@Composable
fun LoadingScreenTypeSwitch(viewModel: PlayerViewModel, tmpMiscSettings: MutableMap<String, Boolean>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        var switched by remember { mutableStateOf(viewModel.showBasicLoadingScreen) }
        LcdText(
            "Basic loading screen",
            viewModel = viewModel
        )
        Switch(
            checked = switched,
            onCheckedChange = {
                switched = !switched
                tmpMiscSettings["showBasicLoadingScreen"] = switched
            },
            colors = SwitchColors(
                checkedThumbColor = Color.White,
                checkedTrackColor = LightLcdGrey,
                checkedBorderColor = Color.White,
                checkedIconColor = viewModel.iconColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = LightLcdGrey,
                uncheckedBorderColor = Color.White,
                uncheckedIconColor = viewModel.iconColor,
                disabledCheckedThumbColor = Color.White,
                disabledCheckedTrackColor = Color.White,
                disabledCheckedBorderColor = Color.White,
                disabledCheckedIconColor = Color.White,
                disabledUncheckedThumbColor = Color.White,
                disabledUncheckedTrackColor = Color.White,
                disabledUncheckedBorderColor = Color.White,
                disabledUncheckedIconColor = Color.White
            )
        )
    }
}
@Composable
fun ResetToDefaultsButton(viewModel: PlayerViewModel, context: Context) {
    Button(
        modifier = Modifier.padding(horizontal = 5.dp),
        onClick = {
            val settingsManager = SettingsManager(context)
            val defaultData = SettingsData(
                customColors = viewModel.customColorMap
            )
            viewModel.updateColor("background", Color(defaultData.backgroundColor))
            viewModel.updateColor("text", Color(defaultData.textColor))
            viewModel.updateColor("icon", Color(defaultData.iconColor))
            viewModel.updateColor("eqLevel", Color(defaultData.eqLevelColor))
            viewModel.updateColor("eqText", Color(defaultData.eqTextColor))
            viewModel.updateColor("sliderThumb", Color(defaultData.sliderThumbColor))
            viewModel.updateColor("sliderTrack", Color(defaultData.sliderTrackColor))
            viewModel.updateShowEqualiser(true)
            viewModel.updateShowBasicLoadingScreen(true)
            settingsManager.saveSettings(defaultData)
        },
        colors = ButtonColors(
            containerColor = LightLcdGrey,
            contentColor = Color.White,
            disabledContainerColor = LightLcdGrey,
            disabledContentColor = Color.White
        )
    ) {
        LcdText(
            "Reset to defaults",
            viewModel = viewModel
        )
    }
}
@Composable
fun CustomColorButton(
    viewModel: PlayerViewModel,
    navController: NavController,
) {
    Button(
        modifier = Modifier.padding(horizontal = 5.dp),
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
@Composable
fun SaveChangesButton(
    tmpColorSettings: MutableMap<String, Int>,
    tmpMiscSettings: MutableMap<String, Boolean>,
    context: Context,
    viewModel: PlayerViewModel
) {
    Button(
        modifier = Modifier
            .padding(horizontal = 5.dp),
        onClick = {
            for (i in tmpColorSettings.keys) {
                if (tmpColorSettings[i] != null) {
                    viewModel.updateColor(i, Color(tmpColorSettings[i]!!))
                }
            }
            viewModel.updateShowEqualiser(
                tmpMiscSettings.getOrElse(
                    "showEqualiser"
                ) { true }
            )
            viewModel.updateShowBasicLoadingScreen(
                tmpMiscSettings.getOrElse(
                    "showBasicLoadingScreen"
                ) { true }
            )
            saveChanges(viewModel, context)
        },
        colors = ButtonColors(
            containerColor = LightLcdGrey,
            contentColor = Color.White,
            disabledContainerColor = LightLcdGrey,
            disabledContentColor = Color.White
        )
    ) {
        LcdText(
            "Save changes",
            viewModel = viewModel
        )
    }
}

fun saveChanges(viewModel: PlayerViewModel, context: Context) {
    val data = SettingsData(
        viewModel.backgroundColor.toArgb(),
        viewModel.textColor.toArgb(),
        viewModel.iconColor.toArgb(),
        viewModel.eqLevelColor.toArgb(),
        viewModel.eqTextColor.toArgb(),
        viewModel.sliderThumbColor.toArgb(),
        viewModel.sliderTrackColor.toArgb(),
        viewModel.customColorMap,
        viewModel.showBasicLoadingScreen,
        viewModel.showEqualiser
    )
    val settingsManager = SettingsManager(context)
    settingsManager.saveSettings(data)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColourListDropDownMenu(
    name: String,
    choice: String,
    viewModel: PlayerViewModel,
    tmpColorSettings: MutableMap<String, Int>,
    currentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(getKeyOfColorMap(currentColor, viewModel)) }
        LcdText(
            "Change $name colour: ",
            viewModel = viewModel
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(140.dp),
        ) {
            TextField(
                modifier = Modifier
                    .menuAnchor(
                        type = MenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    ),
                value = selectedText,
                onValueChange = {  },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LcdGrey.increaseBrightness(0.03f),
                    unfocusedContainerColor = LcdGrey.increaseBrightness(0.03f),
                    focusedTextColor = viewModel.textColor,
                    unfocusedTextColor = viewModel.textColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    color = viewModel.textColor,
                    fontFamily = lcdFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = LcdGrey.increaseBrightness(0.03f),
            ) {
                for (i in viewModel.colorMap.keys) {
                    DropdownMenuItem(
                        text = {
                            LcdText(i, viewModel = viewModel)
                        },
                        onClick = {
                            selectedText = i
                            if (choice !in tmpColorSettings.keys) {
                                tmpColorSettings[choice] = viewModel.colorMap[i]!!.toArgb()
                            } else {
                                tmpColorSettings.remove(choice)
                                tmpColorSettings[choice] = viewModel.colorMap[i]!!.toArgb()
                            }
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColourOtherListDropDownMenu(
    name: String,
    choice: String,
    viewModel: PlayerViewModel,
    tmpColorSettings: MutableMap<String, Int>,
    currentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var expanded by remember { mutableStateOf(false) }
        var selectedText by remember { mutableStateOf(getKeyOfOtherColorMap(currentColor,viewModel)) }
        LcdText(
            "Change $name colour: ",
            viewModel = viewModel
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier
                .width(140.dp)
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
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = LcdGrey.increaseBrightness(0.03f),
                    unfocusedContainerColor = LcdGrey.increaseBrightness(0.03f),
                    focusedTextColor = viewModel.textColor,
                    unfocusedTextColor = viewModel.textColor,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = TextStyle(
                    color = viewModel.textColor,
                    fontFamily = lcdFont,
                    fontWeight = FontWeight.Normal,
                    fontSize = 15.sp
                )
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
                            if (choice !in tmpColorSettings.keys) {
                                tmpColorSettings[choice] = viewModel.otherColorMap[i]!!.toArgb()
                            } else {
                                tmpColorSettings.remove(choice)
                                tmpColorSettings[choice] = viewModel.otherColorMap[i]!!.toArgb()
                            }
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

fun getKeyOfColorMap(colorKey: Color, viewModel: PlayerViewModel): String {
    for (i in viewModel.colorMap.keys) {
        if (viewModel.colorMap[i] == colorKey) {
            return i
        }
    }
    return "Error: Empty map"
}
fun getKeyOfOtherColorMap(colorKey: Color, viewModel: PlayerViewModel): String {
    for (i in viewModel.otherColorMap.keys) {
        if (viewModel.otherColorMap[i] == colorKey) {
            return i
        }
    }
    return "Error: Empty map"
}
@Composable
fun HorizontalColorPicker(viewModel: PlayerViewModel, navController: NavController) {
    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf(Color.White) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.displayCutout),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(viewModel.backgroundColor)
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = "Back arrow"
                    )
                },
                onClick = {
                    navController.popBackStack()
                },
                colors = IconButtonColors(
                    contentColor = viewModel.iconColor,
                    containerColor = Color.Transparent,
                    disabledContentColor = viewModel.iconColor,
                    disabledContainerColor = Color.Transparent
                )
            )
            Spacer(
                modifier = Modifier
                    .width(5.dp)
            )
            LargeLcdText("Color picker", viewModel = viewModel)
        }
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            HsvColorPicker(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight()
                    .padding(10.dp),
                controller = controller,
                onColorChanged = {
                    selectedColor = it.color
                }
            )
            Column(
                modifier = Modifier
                    .width(500.dp)
                    .fillMaxHeight()
                    .padding(horizontal = 5.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AlphaTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    selectedColor = selectedColor,
                    controller = controller
                )
                Spacer(Modifier.height(10.dp))
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
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    var customColorName by remember { mutableStateOf("") }
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
                        modifier = Modifier
                            .padding(horizontal = 5.dp),
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
                            navController.popBackStack()
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
    }
}

@Composable
fun PortraitColorPicker(viewModel: PlayerViewModel, navController: NavController) {
    val controller = rememberColorPickerController()
    var selectedColor by remember { mutableStateOf(Color.White) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(viewModel.backgroundColor),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(viewModel.backgroundColor)
                .windowInsetsPadding(WindowInsets.statusBars),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                content = {
                    Icon(
                        painterResource(R.drawable.arrow_back),
                        contentDescription = "Back arrow"
                    )
                },
                onClick = {
                    navController.popBackStack()
                },
                colors = IconButtonColors(
                    contentColor = viewModel.iconColor,
                    containerColor = Color.Transparent,
                    disabledContentColor = viewModel.iconColor,
                    disabledContainerColor = Color.Transparent
                )
            )
            Spacer(
                modifier = Modifier
                    .width(5.dp)
            )
            LargeLcdText("Color picker", viewModel = viewModel)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
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
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            var customColorName by remember { mutableStateOf("") }
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
                modifier = Modifier
                    .padding(horizontal = 5.dp),
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
                    navController.popBackStack()
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
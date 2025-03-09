package com.example.coeating

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BakingScreen(
    onTakePhotoClick: () -> Unit,
    apiResult: String,
    isKetoSelected: Boolean,
    onToggleKeto: () -> Unit,
    isPaleoSelected: Boolean,
    onTogglePaleo: () -> Unit,
    isVeganSelected: Boolean,
    onToggleVegan: () -> Unit,
    isHalalSelected: Boolean,
    onToggleHalal: () -> Unit,
    isWholeFoodsSelected: Boolean,
    onToggleWholeFoods: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Options for dietary preferences.
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(checked = isKetoSelected, onCheckedChange = { onToggleKeto() })
            Text(text = "Keto")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(checked = isPaleoSelected, onCheckedChange = { onTogglePaleo() })
            Text(text = "Paleo")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(checked = isVeganSelected, onCheckedChange = { onToggleVegan() })
            Text(text = "Vegan")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(checked = isHalalSelected, onCheckedChange = { onToggleHalal() })
            Text(text = "Halal")
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Checkbox(checked = isWholeFoodsSelected, onCheckedChange = { onToggleWholeFoods() })
            Text(text = "Whole Foods")
        }

        Button(onClick = onTakePhotoClick, modifier = Modifier.padding(16.dp)) {
            Text(text = "Take Photo")
        }
        if (apiResult.isNotEmpty()) {
            Text(
                text = "Co-Eating says: $apiResult",
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

package com.example.saliendodecasa.checklist.presentation

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.saliendodecasa.MainApplication

class MainActivity : ComponentActivity() {
    private val sharedPreferences by lazy {
        getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private val viewModel: MainViewModel by viewModels {
        viewModelFactory {
            initializer {
                val application: MainApplication =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MainApplication)
                MainViewModel(application.itemRepository, sharedPreferences)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChecklistScreen(viewModel)
        }
    }
}

@Composable
fun ChecklistScreen(viewModel: MainViewModel) {
    val uiState: State<MainUiState> = viewModel.uiState.collectAsState()
    val newItemText: MutableState<String> = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        val categories: List<String> = listOf("General", "Trabajo", "Ejercicio", "Estudio")

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            categories.forEach { category ->
                val isSelected: Boolean = uiState.value.currentListName == category

                if (isSelected) {
                    Button(
                        onClick = {
                            viewModel.changeList(category)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        Text(
                            text = category,
                            maxLines = 1
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            viewModel.changeList(category)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        Text(
                            text = category,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newItemText.value,
                onValueChange = { newItemText.value = it },
                label = { Text("Nuevo Objeto...") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    viewModel.addItem(newItemText.value)
                    newItemText.value = ""
                }
            ) {
                Text("Añadir")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.resetChecks() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("¡Todo Listo!")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(uiState.value.items) { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleCheck(item) }
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                ) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = null,
                        modifier = Modifier.scale(1.3f)
                    )
                    Text(
                        text = item.name,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 12.dp)
                    )
                    TextButton(onClick = { viewModel.deleteItem(item) }) {
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}
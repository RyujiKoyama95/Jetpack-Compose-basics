/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codelab.basics

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.support.v4.os.IResultReceiver2.Default
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.codelab.basics.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentでレイアウトを設定するが、XMLファイルではなく、@Composable関数を呼び出す
        // XMLファイルを指定する場合はsetContentView()
        setContent { 
            BasicsCodelabTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }

    @Composable
    private fun MyApp(names: List<String> = listOf("name", "aa", "compose"), modifier: Modifier = Modifier) {
        Column(modifier = modifier.padding(vertical = 4.dp)) {
            for (name in names) {
                Greeting(name = name)
            }
        }
    }

    @Preview(showBackground = true, name = "text preview")
    @Composable
    private fun DefaultPreview() {
        BasicsCodelabTheme {
            MyApp()
        }
    }

    @Composable
    private fun Greeting(name: String) {
        // 状態保持のためexpandedフラグを定義。
        // 単純にフラグ追加しても、Composeは状態変更として検出しない。
        // state/MutableStateは何らかの値を保持し、値が変更した場合は再Compose(UI更新)するインターフェース。
        // しかし、再コンポジションが随時発生する可能性があるため、フラグはリセットされてしまう問題がある。
        // 再コンポジションの前後で状態を保持するにはrememberを使用して、状態を保護する。リセットされない。
        // 状態が変化すると、自動的に再コンポーズする。
        // また、同じコンポーズを別々の部分から呼び出すと、異なるUIが生成され、状態も別々になる。
        // 例えば今回の場合だと、ボタンが複数あるため、それぞれで固有の状態を保持する。
        val expanded = remember { mutableStateOf(false) }
        val extraPadding = if (expanded.value) {
            48.dp
        } else {
            0.dp
        }
        // Surfaceは色を受け取る。
        // Surfaceの中にネストされたコンポーネント(ここではText)は、背景色の上に描画される。
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
            Row(modifier = Modifier.padding(24.dp)) {
                // SurfaceやTextなどのほとんどのCompose UI要素は引数にmodifier(修飾子)を持っている。
                // 1つの要素に複数の修飾子をつけるときは、繋げればいい。
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(bottom = extraPadding)
                ) {
                    // modifierは親レイアウト内での配置、表示、動作を指定できる。
                    Text(text = "Hello!")
                    Text(text = "$name")
                }
                ElevatedButton(onClick = { expanded.value = !expanded.value }) {
                    Text(if (expanded.value) "show less" else "show more")
                }
            }
        }
    }
    @Composable
    private fun OnboardingScreen(modifier: Modifier = Modifier) {
        // =ではなくbyにすることで、.valueでアクセスせずに済む
        var shouldShowScreen by remember { mutableStateOf(true) }
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Welcome to the Basics Codelab!")
            Button(
                modifier = modifier.padding(24.dp),
                onClick = { shouldShowScreen = false }
            ) {
                Text(text = "Continue")
            }
        }
    }

    @Preview
    @Composable
    private fun OnboardingPreview() {
        BasicsCodelabTheme {
            OnboardingScreen()
        }
    }
}
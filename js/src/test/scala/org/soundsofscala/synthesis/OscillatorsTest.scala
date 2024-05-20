/*
 * Copyright 2024 Sounds of Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.soundsofscala.synthesis

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class OscillatorsTest extends AnyFunSuite with Matchers with BeforeAndAfterAll:

  // This will be executed before all tests
  override def beforeAll(): Unit = ()
  // Perform setup code here, for example, creating an AudioContext

  // This will be executed after all tests
  override def afterAll(): Unit = ()
  // Perform cleanup code here

  test("sawtooth wave") {}

//    val sawtooth = SawtoothOscillator(110)

/*
 * Copyright 2014 Philip Cronje
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package net.za.slyfox.minecraft

import net.za.slyfox.minecraft.nbt.NbtCompound
import net.za.slyfox.minecraft.nbt.NbtList
import net.za.slyfox.minecraft.nbt.NbtNumber
import net.za.slyfox.minecraft.nbt.NbtString
import net.za.slyfox.minecraft.nbt.NbtValue

class NbtPrinter extends IndentPrinter {

	NbtPrinter() {

		super(new PrintWriter(System.out), '\t')
	}

	void print(NbtCompound compound) {

		printIndent(); println("TAG_Compound(${formatName compound}): ${compound.size()} entries")
		printIndent(); println('{')

		incrementIndent()
		try {
			compound.values().each { print it }
		} finally {
			decrementIndent()
		}

		printIndent(); println('}')
	}

	protected void print(NbtList list) {

		printIndent(); println("TAG_List(${formatName list}): ${list.size()} entries")
		printIndent(); println('{')

		incrementIndent()
		try {
			list.each { print it }
		} finally {
			decrementIndent()
		}

		printIndent(); println('}')
	}

	protected void print(NbtNumber nbtNumber) {

		printIndent(); print('TAG_')
		def number = nbtNumber.number
		if(number instanceof Byte) {
			print('Byte')
		} else if(number instanceof Double) {
			print('Double')
		} else if(number instanceof Float) {
			print('Float')
		} else if(number instanceof Integer) {
			print('Integer')
		} else if(number instanceof Long) {
			print('Long')
		} else if(number instanceof Short) {
			print('Short')
		} else {
			print('NUMBER')
		}
		println("(${formatName nbtNumber}): ${number}")
	}

	protected void print(NbtValue value) {

		if(value instanceof NbtCompound) {
			print(value)
		} else if(value instanceof NbtList) {
			print(value)
		} else if(value instanceof NbtNumber) {
			print(value)
		} else if(value instanceof NbtString) {
			printIndent()
			println("TAG_String(${formatName value}): '${value.string}'")
		} else {
			printIndent()
			println("TAG_UNKNOWN(${formatName value})")
		}
	}

	protected static formatName(NbtValue value) {

		return (value.getName() != null) ? "'${value.getName()}'" : 'None'
	}
}
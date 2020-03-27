/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package com.radixdlt.client.core.ledger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.radixdlt.client.core.atoms.Atom;
import com.radixdlt.serialization.DsonOutput;
import com.radixdlt.serialization.DsonOutput.Output;
import com.radixdlt.serialization.SerializerConstants;
import com.radixdlt.serialization.SerializerDummy;
import com.radixdlt.serialization.SerializerId2;
@SerializerId2("api.atom_event")
public class AtomEvent {

	// Placeholder for the serializer ID
	@JsonProperty(SerializerConstants.SERIALIZER_NAME)
	@DsonOutput(DsonOutput.Output.ALL)
	private SerializerDummy serializer = SerializerDummy.DUMMY;

	@JsonProperty("version")
	@DsonOutput(DsonOutput.Output.ALL)
	private short version = 100;

	public enum AtomEventType {
		STORE, DELETE
	}

	@JsonProperty("atom")
	@DsonOutput(Output.ALL)
	private Atom atom;

	private transient AtomEventType type;

	private AtomEvent() {
		this.atom = null;
		this.type = null;
	}

	public AtomEvent(Atom atom, AtomEventType type) {
		this.atom = atom;
		this.type = type;
	}

	public Atom getAtom() {
		return atom;
	}

	public AtomEventType getType() {
		return type;
	}

	@JsonProperty("type")
	@DsonOutput(Output.ALL)
	private String getTypeString() {
		return this.type.name().toLowerCase();
	}

	@JsonProperty("type")
	private void setTypeString(String type) {
		this.type = AtomEventType.valueOf(type.toUpperCase());
	}
}

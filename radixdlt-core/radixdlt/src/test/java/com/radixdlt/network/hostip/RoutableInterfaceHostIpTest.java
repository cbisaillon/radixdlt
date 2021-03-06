/*
 * (C) Copyright 2020 Radix DLT Ltd
 *
 * Radix DLT Ltd licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the
 * License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.  See the License for the specific
 * language governing permissions and limitations under the License.
 */

package com.radixdlt.network.hostip;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Vector;

import org.junit.Test;

import com.google.common.collect.Iterators;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RoutableInterfaceHostIpTest {

	@Test
	public void testEmpty() {
		RoutableInterfaceHostIp rihip = make();
		assertFalse(rihip.hostIp(Collections.emptyIterator()).isPresent());
	}

	@Test
	public void testLoopback() throws UnknownHostException {
		RoutableInterfaceHostIp rihip = make();

		InetAddress loopback = InetAddress.getByName("127.0.0.1");
		Vector<InetAddress> addresses = new Vector<>(Arrays.asList(loopback));

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getInetAddresses()).thenReturn(addresses.elements());

		assertFalse(rihip.hostIp(Iterators.forArray(ni)).isPresent());
	}

	@Test
	public void testLinkLocal() throws UnknownHostException {
		RoutableInterfaceHostIp rihip = make();

		InetAddress linklocal = InetAddress.getByName("169.254.0.0");
		Vector<InetAddress> addresses = new Vector<>(Arrays.asList(linklocal));

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getInetAddresses()).thenReturn(addresses.elements());

		assertFalse(rihip.hostIp(Iterators.forArray(ni)).isPresent());
	}

	@Test
	public void testMulticast() throws UnknownHostException {
		RoutableInterfaceHostIp rihip = make();

		InetAddress linklocal = InetAddress.getByName("224.0.0.1");
		Vector<InetAddress> addresses = new Vector<>(Arrays.asList(linklocal));

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getInetAddresses()).thenReturn(addresses.elements());

		assertFalse(rihip.hostIp(Iterators.forArray(ni)).isPresent());
	}

	@Test
	public void testNonRoutable() throws UnknownHostException {
		RoutableInterfaceHostIp rihip = make();

		InetAddress nonroutable = InetAddress.getByName("192.168.0.1");
		Vector<InetAddress> addresses = new Vector<>(Arrays.asList(nonroutable));

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getInetAddresses()).thenReturn(addresses.elements());

		Optional<String> result = rihip.hostIp(Iterators.forArray(ni));
		assertFalse(result.isPresent());
	}

	@Test
	public void testRoutable() throws UnknownHostException {
		RoutableInterfaceHostIp rihip = make();

		InetAddress routable = InetAddress.getByName("8.8.8.8");
		Vector<InetAddress> addresses = new Vector<>(Arrays.asList(routable));

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getInetAddresses()).thenReturn(addresses.elements());

		Optional<String> result = rihip.hostIp(Iterators.forArray(ni));
		assertTrue(result.isPresent());
		assertEquals("8.8.8.8", result.get());
	}

	@Test
	public void testTooManyAddresses() throws UnknownHostException {
		RoutableInterfaceHostIp rihip = make();

		InetAddress addr1 = InetAddress.getByName("8.8.8.8");
		InetAddress addr2 = InetAddress.getByName("8.8.4.4");
		Vector<InetAddress> addresses = new Vector<>(Arrays.asList(addr1, addr2));

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getInetAddresses()).thenReturn(addresses.elements());

		Optional<String> result = rihip.hostIp(Iterators.forArray(ni));
		assertFalse(result.isPresent());
	}

	@Test
	public void testInvalidAddress() {
		// Not sure how this can really happen without a O/S fail
		RoutableInterfaceHostIp rihip = make();

		InetAddress badAddr = mock(InetAddress.class);
		when(badAddr.getHostAddress()).thenReturn("a:b");
		Vector<InetAddress> addresses = new Vector<>(Arrays.asList(badAddr));

		NetworkInterface ni = mock(NetworkInterface.class);
		when(ni.getInetAddresses()).thenReturn(addresses.elements());

		Optional<String> result = rihip.hostIp(Iterators.forArray(ni));
		assertFalse(result.isPresent());
	}

	private static RoutableInterfaceHostIp make() {
		return (RoutableInterfaceHostIp) RoutableInterfaceHostIp.create();
	}
}

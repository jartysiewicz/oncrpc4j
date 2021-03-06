/*
 * Copyright (c) 2009 - 2015 Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.dcache.xdr;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class OncRpcClient implements AutoCloseable {

    private final InetSocketAddress _socketAddress;
    private final OncRpcSvc _rpcsvc;

    public OncRpcClient(InetAddress address, int protocol, int port) {
        this(new InetSocketAddress(address, port), protocol, 0, IoStrategy.SAME_THREAD);
    }

    public OncRpcClient(InetAddress address, int protocol, int port, int localPort) {
        this(new InetSocketAddress(address, port), protocol, localPort, IoStrategy.SAME_THREAD);
    }

    public OncRpcClient(InetAddress address, int protocol, int port, int localPort, IoStrategy ioStrategy) {
        this(new InetSocketAddress(address, port), protocol, localPort, ioStrategy);
    }

    public OncRpcClient(InetSocketAddress socketAddress, int protocol) {
        this(socketAddress, protocol, 0, IoStrategy.SAME_THREAD);
    }

    public OncRpcClient(InetSocketAddress socketAddress, int protocol, int localPort, IoStrategy ioStrategy) {

        _socketAddress = socketAddress;
        _rpcsvc = new OncRpcSvcBuilder()
                .withClientMode()
                .withPort(localPort)
                .withIpProtocolType(protocol)
                .withIoStrategy(ioStrategy)
                .build();
    }

    public XdrTransport connect() throws IOException {
        return connect(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
    }

    public XdrTransport connect(long timeout, TimeUnit timeUnit) throws IOException {
        XdrTransport t;
        try {
        _rpcsvc.start();
            t =_rpcsvc.connect(_socketAddress, timeout, timeUnit);
        } catch (IOException e ) {
            _rpcsvc.stop();
            throw e;
        }
        return t;
    }

    @Override
    public void close() throws IOException {
        _rpcsvc.stop();
    }
}

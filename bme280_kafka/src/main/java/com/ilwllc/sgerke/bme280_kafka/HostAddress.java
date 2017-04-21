package com.ilwllc.sgerke.bme280_kafka;
import java.net.*;
import java.util.*;

public class HostAddress
{
	public String value() throws Exception
        {
                String Value = "Host address unknown";
				Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
                for (; n.hasMoreElements();)
                {
                        NetworkInterface e = n.nextElement();
                         Enumeration<InetAddress> a = e.getInetAddresses();
                        for (; a.hasMoreElements();)
                        {
                                InetAddress addr = a.nextElement();
                                 
                                if (addr.isSiteLocalAddress() && addr.isReachable(3000)) {
                                	Value = addr.getHostAddress().toString();
                                }
                        }
                }
                return Value;
        }
}

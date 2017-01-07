# InetAddressUtil

```java
InetAddressUtil inetAddressUtil = new InetAddressUtil( "1.234.56.7/28" );

// get values as InetAddress
InetAddress inetAddress = inetAddressUtil.getInetAddress();
InetAddress subnetMask = inetAddressUtil.getsubnetMask();
InetAddress networkAddress = inetAddressUtil.getNetworkAddress();
InetAddress broadcastAddress = inetAddressUtil.getBroadcastAddress();

// get values as String
String inetAddressStr = inetAddressUtil.getInetAddressStr();
String subnetMaskStr = inetAddressUtil.getsubnetMaskStr();
String networkAddressStr = inetAddressUtil.getNetworkAddressStr();
String broadcastAddressStr = inetAddressUtil.getBroadcastAddressStr();

// compare to another address
inetAddressUtil.equals( "1.234.56.7" );
inetAddressUtil.includes( "1.234.56.7" );
```

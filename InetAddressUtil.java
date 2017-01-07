package InetAddressUtil;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.math.BigInteger;

public class InetAddressUtil
{
	private static final String IPV4 = "IPv4";
	private static final String IPV6 = "IPv6";

	private String ipVersion            = null;
	private String inetAddressStr       = null;
	private String onesComplementOfZero = null;

	private InetAddress inetAddress      = null;
	private InetAddress subnetMask       = null;
	private InetAddress networkAddress   = null;
	private InetAddress broadcastAddress = null;

	private int prefixLength       = -1;
	private int byteLength         = -1;
	private int bitwiseShiftLength = -1;

	public InetAddressUtil( InetAddress tmpInetAddress ) throws UnknownHostException {
		InetAddressUtilConstructor( tmpInetAddress.getHostAddress().toString() );
	}

	public InetAddressUtil( String tmpInetAddressStr ) throws UnknownHostException {
		InetAddressUtilConstructor( tmpInetAddressStr );
	}

	public void InetAddressUtilConstructor( String tmpInetAddressStr ) throws UnknownHostException {
		if( tmpInetAddressStr.matches( "^[0-9a-f.:]+" ) ){
			inetAddressStr = tmpInetAddressStr;
		} else if( tmpInetAddressStr.matches( "^[0-9a-f.:]+/[0-9]+$" ) ){
			String[] addrAndPrefixLength = tmpInetAddressStr.split( "/" );
			inetAddressStr = addrAndPrefixLength[0];
			prefixLength = Integer.parseInt( addrAndPrefixLength[1] );
		} else {
			throw new UnknownHostException( tmpInetAddressStr + " is not a valid IP address string" );
		}

		inetAddress = InetAddress.getByName( inetAddressStr );
		if ( inetAddress instanceof Inet4Address ){
			ipVersion = IPV4;
			if( prefixLength == -1 ){
				prefixLength = 32;
			}
			byteLength = 4;
			bitwiseShiftLength = 32 - prefixLength;
			onesComplementOfZero = "255.255.255.255";
		} else if ( inetAddress instanceof Inet6Address ) {
			ipVersion = IPV6;
			if( prefixLength == -1 ){
				prefixLength = 128;
			}
			byteLength = 16;
			bitwiseShiftLength = 128 - prefixLength;
			onesComplementOfZero = "ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff";
		} else {
			throw new UnknownHostException( inetAddressStr + " is not a valid IP address string" );
		}

		BigInteger inetAddressValue = new BigInteger( 1, inetAddress.getAddress() );
		BigInteger subnetMaskValue = (new BigInteger( 1, InetAddress.getByName( onesComplementOfZero ).getAddress() )).shiftLeft( bitwiseShiftLength );
		byte[] subnetMaskBytes = subnetMaskValue.toByteArray();
		subnetMask = InetAddress.getByAddress( toBytes( subnetMaskBytes, byteLength ) );
		byte[] networkAddressBytes = inetAddressValue.and( subnetMaskValue ).toByteArray();
		networkAddress = InetAddress.getByAddress( toBytes( networkAddressBytes, byteLength ) );
		byte[] broadcastAddressBytes = inetAddressValue.or( subnetMaskValue.not() ).toByteArray();
		broadcastAddress = InetAddress.getByAddress( toBytes( broadcastAddressBytes, byteLength ) );
	}

	public boolean isIPv4 () {
		return ipVersion.equals( IPV4 );
	}

	public boolean isIPv6 () {
		return ipVersion.equals( IPV6 );
	}

	public InetAddress getInetAddress () {
		return inetAddress;
	}

	public String getInetAddressStr () {
		return inetAddressStr;
	}

	public int getPrefixLength () {
		return prefixLength;
	}

	public String getPrefixLengthStr () {
		return String.valueOf( prefixLength );
	}

	public InetAddress getNetworkAddress () {
		return networkAddress;
	}

	public String getNetworkAddressStr () {
		return networkAddress.getHostAddress().toString();
	}

	public InetAddress getBroadcastAddress () {
		return broadcastAddress;
	}

	public String getBroadcastAddressStr () {
		return broadcastAddress.getHostAddress().toString();
	}

	public InetAddress getSubnetMask () {
		return subnetMask;
	}

	public String getSubnetMaskStr () {
		return subnetMask.getHostAddress().toString();
	}

	public boolean equals (InetAddress targetInetAddress) {
		return inetAddress.equals( targetInetAddress );
	}

	public boolean equals (String targetInetAddressStr) {
		return inetAddressStr.equals( targetInetAddressStr );
	}

	public boolean includes (InetAddress targetInetAddress) {
		InetAddressUtil targetInetAddressUtil;
		InetAddressUtil sameSubnetTargetInetAddressUtil;
		try {
			targetInetAddressUtil = new InetAddressUtil( targetInetAddress );
			sameSubnetTargetInetAddressUtil = new InetAddressUtil( targetInetAddressUtil.getInetAddressStr() + "/" + prefixLength );
		} catch (UnknownHostException e) {
			return false;
		}
		return ((prefixLength <= targetInetAddressUtil.getPrefixLength()) && networkAddress.equals( sameSubnetTargetInetAddressUtil.getNetworkAddress() ));
	}

	public boolean includes (String targetInetAddressStr) {
		InetAddressUtil targetInetAddressUtil;
		InetAddressUtil sameSubnetTargetInetAddressUtil;
		try {
			targetInetAddressUtil = new InetAddressUtil( targetInetAddressStr );
			sameSubnetTargetInetAddressUtil = new InetAddressUtil( targetInetAddressUtil.getInetAddressStr() + "/" + prefixLength );
		} catch (UnknownHostException e) {
			return false;
		}
		return ((prefixLength <= targetInetAddressUtil.getPrefixLength()) && networkAddress.equals( sameSubnetTargetInetAddressUtil.getNetworkAddress() ));
	}

	private byte[] toBytes (byte[] input_bytes, int size) {
		int input_bytes_size = input_bytes.length;
		byte[] output_bytes = new byte[size];

		for( int i=0; i<size; i++ ){
			int index = i + (input_bytes_size - size);
			if (index < 0) {
				output_bytes[i] = 0;
			} else {
				output_bytes[i] = input_bytes[i + (input_bytes_size - size)];
			}
		}

		return output_bytes;
	}
}

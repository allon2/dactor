package cn.ymotel.dactor.action.httpclient;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public  class TrustAllHostnames implements HostnameVerifier {

		@Override
		public boolean verify(String s, SSLSession sslSession) {
			return true;
		}
	}
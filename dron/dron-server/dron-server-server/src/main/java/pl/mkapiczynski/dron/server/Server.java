package pl.mkapiczynski.dron.server;

import pl.mkapiczynski.dron.user.UserBeanImpl;

public class Server {

	public static void main(String[] args) {
		UserBeanImpl service = new UserBeanImpl();
		service.print("Hello");

	}

}

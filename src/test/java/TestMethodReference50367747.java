/*
 * Copyright (c) 2018 Oleg Sklyar. All rights reserved
 */

import org.junit.Test;


public class TestMethodReference50367747 {

	interface Telling {
		void tell(String what);
	}

	private static void tellSomething(String what) {
		System.out.println("something: " + what);
	}

	private void tellMore(String what) {
		System.out.println("more: " + what);
	}

	private void tellEvenMore(String what) {
		System.out.println("even more: " + what);
	}

	@Test
	public void teller() {
		Telling telling = new Telling() {
			@Override
			public void tell(String what) {
				System.out.println("anonymous: " + what);
			}
		};
		telling.tell("default");

		telling = (what) -> System.out.println("what: " + what);
		telling.tell("before hello");

		telling = TestMethodReference50367747::tellSomething;
		telling.tell("hello");

		telling = this::tellMore;
		telling.tell("hello again");

		telling = new TestMethodReference50367747()::tellEvenMore;
		telling.tell("and again");
	}

}

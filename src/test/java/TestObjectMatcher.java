/*
 * Copyright (c) 2018 Oleg Sklyar. All rights reserved
 */

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.junit.Test;


public class TestObjectMatcher {

	public static class Student {

		public String name;

		public String course;

		public Double grade;

		public Student() {}

		public Student(String name, String course, Double grade) {
			this.name = name;
			this.course = course;
			this.grade = grade;
		}
	}

	static class ObjectMatcher {

		// NPE on null items
		// assumes public default constructor for T is available
		public <T> T match(Collection<T> items, T defaults) {
			if (items.isEmpty()) {
				return defaults;
			}
			try {
				@SuppressWarnings("unchecked")
				Class<T> clazz = (Class<T>) items.iterator().next().getClass();
				Field[] fields = clazz.getDeclaredFields();

				T res = clazz.newInstance();

				for (Field field : fields) {
					boolean firstItem = true;
					Object match = null;
					for (T item : items) {
						Object value = field.get(item);
						if (firstItem) {
							match = value;
						}
						else if (!Objects.equals(value, match)) {
							match = field.get(defaults);
							break;
						} // otherwise keep the match as is
						firstItem = false;
					}
					field.set(res, match);
				}
				return res;
			}
			catch (IllegalAccessException | InstantiationException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Test
	public void match_onMistmatchMatchAndNull_ok() {
		Student s1 = new Student("Andrew", "Physics", null);
		Student s2 = new Student("Joe", "Physics", 3.45);
		Student s3 = new Student("Nicki", "Physics", 2.39);

		Student defaults = new Student("Multiple Names", "Multiple Courses", 1.0);

		ObjectMatcher matcher = new ObjectMatcher();
		Student res = matcher.match(Arrays.asList(s1, s2, s3), defaults);

		assertEquals("Multiple Names", res.name);
		assertEquals("Physics", res.course);
		assertEquals(1.0, res.grade.doubleValue(), 0.001);
	}

}

package org.dbdoclet.doclet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.dbdoclet.doclet.annotation.Param;

public class MethodArguments {

	/**
	 * Testmethode um Methodenargumente in dbddoclet zu testen.
	 * 
	 * Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo
	 * ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis
	 * parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec,
	 * pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec
	 * pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo,
	 * rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede
	 * mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper
	 * nisi. Aenean vulputate eleifend tellus. Aenean leo ligula, porttitor eu,
	 * consequat vitae, eleifend ac, enim. Aliquam lorem ante, dapibus in, viverra
	 * quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet.
	 * Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur
	 * ullamcorper ultricies nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus,
	 * tellus eget condimentum rhoncus, sem quam semper libero, sit amet adipiscing
	 * sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit
	 * id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut
	 * libero venenatis faucibus. Nullam quis ante. Etiam sit amet orci eget eros
	 * faucibus tincidunt. Duis leo. Sed fringilla mauris sit amet nibh. Donec
	 * sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit
	 * cursus nunc,
	 * 
	 * @param param1 Ein <b>Parameter</b> und ein Link auf {@link #test_2(int, float, boolean, String)}.
	 * @return
	 */
	public int test_1(String param1) {
		return 42;
	}

	/**
	 * 
	 * @param paramInt
	 * @param paramFloat
	 * @param paramBooleanPrimitive
	 * @param paramString
	 * @return
	 */
	public int test_2(int paramInt, float paramFloat, boolean paramBooleanPrimitive, String paramString) {
		return 42;
	}

	/**
	 * 
	 * @param opts Variable number of options. 
	 * @return int 
	 */
	public int test_3(String... opts) {
		return 42;
	}

	public int test_4(@Param String paramWithAnnotation) {
		return 42;
	}

	/**
	 * Generics and Supplier
	 * 
	 * @param <T> Generic type.
	 * @param supplier Functional interface
	 * @return List of T.
	 */
	public <T extends AbstractBase> List<T> test_5(Supplier<List<T>> supplier, ArrayList<? extends Comparable<String>> comparableList) {
		return supplier.get();
	}

	public void test_6(ArrayList<? extends Comparable<String>> comparableList) {
		return;
	}

	/**
	 * Testing Arrays as method arguments.
	 * 
	 * @param array1
	 * @param array2
	 */
	public void test_7(String[] array1, int[][] array2) {
		return;
	}
}

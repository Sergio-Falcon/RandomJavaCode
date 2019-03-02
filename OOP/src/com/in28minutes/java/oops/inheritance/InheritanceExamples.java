package com.in28minutes.java.oops.inheritance;
import java.util.*;

abstract class Animal {
	String name;

	//cool functionality
	abstract String bark();
}

class Dog extends Animal {
	String bark() {
		return "Bow Bow";
	}
}

class Cat extends Animal {
	String bark() {
		return "Meow Meow";
	}
}

public class InheritanceExamples {
	public static void main(String[] args) {
		//Animal[] animal = new Cat();
		LinkedList<Animal> animalList = new LinkedList<Animal>();
		animalList.add(new Cat());
		animalList.add(new Dog());
		animalList.add(new Dog());
		//System.out.println(animalList.get(1).bark());
	
		ListIterator<Animal> listIterator = animalList.listIterator();
		while(listIterator.hasNext()){
			System.out.println(listIterator.next().bark());
		}
		System.out.println("\nDiff way now!\n");
//		for(int i = 0; i < animalList.size(); i++){
//			System.out.println(animalList.get(i).bark());
//		}
		
		/** Now in reverse! **/
		System.out.println("\nIn Reverse:\n");

		for(int i = animalList.size() - 1; i >= 0; i--){
			System.out.println(animalList.get(i).bark());

		}
	}
}

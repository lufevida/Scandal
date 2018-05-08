package language.interfaces;

public interface ObjectObject {

	Object apply(Object x);

	default ObjectObject then(ObjectObject after) {
		return x -> after.apply(apply(x));
	}

}

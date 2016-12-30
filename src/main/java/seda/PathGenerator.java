package seda;

import java.util.List;

public interface PathGenerator<A, B> {

    public List<A> generatePaths(B object);
}

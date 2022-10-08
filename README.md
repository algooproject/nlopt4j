
## > Installation step

* Step 1. Download the tar from https://nlopt.readthedocs.io/en/latest/#download-and-installation
* Step 2. unzip to `~nlopt` as [nlopt4j](https://github.com/dibyendumajumdar/nlopt4j) mentioned
  * `mv nlopt-2.6.2.zip ~`
  * `cd ~`
  * `unzip nlopt-2.6.2.zip`
  * `mv nlopt-2.6.2 nlopt`
  * `cd nlopt`
* Step 3. cmake
  *  `cmake . && make && sudo make install` inside ~/nlopt
* Step 4. Create include and lib then put related file inside it (path defined by [nlopt4j](https://github.com/dibyendumajumdar/nlopt4j)]])
  * `mkdir include`
  * `mkdir lib`
  * `cp *.in include`
  * `cp *.dylib lib`

## How to run?

In order to run the test in command line in macOS, use the following:

./mvnw clean test -Dhome.path=$HOME -Dalgoproject.path=<algo-project-path>

e.g.

./mvnw clean test -Dhome.path=$HOME -Dalgoproject.path=/Users/n/Dev/git/Algo-Project

Basically, the nlopt needs to do optimization by calling Stock-analysis.jar to run, and it is referenced as maven depedency (ref nlopt4j -> pom.xml)

## How to update Stock-analysis.jar?

How to put algo-project.jar to maven, see algo project repo -> README.md

## What is algoproject.path (as system variable)?

Why need `-Dalgoproject.path`? Because Stock-analysis.jar need to read the stock history file when doint optimization, and currently need to refer to the Algo Project repo to read the file. You can see `algoproject.path` in Algo Project -> Constants.java for how to get algoproject.path

> What it is the optimizer doing?

* MainForNLOPT.class return 0 - profit
* In nlopt, it set the minObjective, meaning trying to optmize to find the input values to get the lowerest (0-profit) output value
  * It means to trying to get the input values results in maximum profit

## Troubleshooting: Could not initialize class org.nlopt4j.optimizer.NLopt

* Need to use oracle jdk, not adoptopenjdk
* Bug ticket for this: Reference: https://github.com/AdoptOpenJDK/openjdk-build/issues/489
* Error if using adoptopenjdk:

```
[INFO] Running org.nlopt4j.optimizer.TestNLopt
[ERROR] Tests run: 1, Failures: 0, Errors: 1, Skipped: 0, Time elapsed: 0.002 s <<< FAILURE! - in org.nlopt4j.optimizer.TestNLopt
[ERROR] org.nlopt4j.optimizer.TestNLopt.testTutorialSample  Time elapsed: 0 s  <<< ERROR!
java.lang.NoClassDefFoundError: Could not initialize class org.nlopt4j.optimizer.NLopt
        at org.nlopt4j.optimizer.TestNLopt.testTutorialSample(TestNLopt.java:53)
```

## Troubleshooting: `ld: framework not found JavaVM` occurs

- It is related to macOS and XCode and jvm stuff
- Reference: https://stackoverflow.com/a/67313468
- You can safely replace `-framework JavaVM` with `-framework JavaNativeFoundation`. It worked for me.
  in maven-macosx/pom.xml

```
<linkerStartOptions>  
  <linkerStartOption>${linker.debug.options}</linkerStartOption>  
  <!--<linkerStartOption>-dynamiclib -framework JavaVM ${nlopt.library}</linkerStartOption>-->  
  <linkerStartOption>-dynamiclib -framework JavaNativeFoundation ${nlopt.library}</linkerStartOption>  
</linkerStartOptions>
```

---

Below is standup introduction for nlopt4j:

nlopt4j - NLopt for Java
========================

JNI wrapper for NLopt (http://ab-initio.mit.edu/wiki/index.php/NLopt).

Example
=======

The following example is a port of the sample used in NLopt tutorial.

```Java
static final class Constraint implements NLopt.NLopt_func {
    double a, b;
    Constraint(double a, double b) {
        this.a = a;
        this.b = b;
    }
    @Override
    public double execute(double[] x, double[] gradient) {
        double a = this.a, b = this.b;
        if (gradient.length == x.length) {
            gradient[0] = 3.0 * a * (a*x[0] + b) * (a*x[0] + b);
            gradient[1] = -1.0;
        }
        return ((a*x[0] + b) * (a*x[0] + b) * (a*x[0] + b) - x[1]);
    }
}

@Test
public void testTutorialSample() {
    NLopt.NLopt_func func = new NLopt.NLopt_func() {
        @Override
        public double execute(double[] x, double[] gradient) {
            if (gradient.length == x.length) {
                gradient[0] = 0.0;
                gradient[1] = 0.5 / Math.sqrt(x[1]);
            }
            return Math.sqrt(x[1]);
        }
    };

    double[] lb = {Double.NEGATIVE_INFINITY, 0}; /* lower bounds */
    NLopt optimizer = new NLopt(NLopt.NLOPT_LD_MMA, 2);
    optimizer.setLowerBounds(lb);
    optimizer.setMinObjective(func);
    optimizer.addInequalityConstraint(new Constraint(2.0, 0.0), 1e-8);
    optimizer.addInequalityConstraint(new Constraint(-1.0, 1.0), 1e-8);
    optimizer.setRelativeToleranceOnX(1e-4);
    double[] x = {1.234, 5.678};  /* some initial guess */
    NLoptResult minf = optimizer.optimize(x);
    System.out.println(String.format("found minimum at f(%f,%f) = %f\n", x[0], x[1], minf.minValue()));
    assertEquals(0.544331, minf.minValue(), 1e-4);
}
```

Build Instructions
==================

Currently build has been tested on Windows and Mac OS X (Snow Leopard).

32-bit Windows
--------------
Requires following:
+ Visual C++ 2013 (should work with older versions but not tested)
+ CMake 2.8.12 or above.
+ Java 1.6 or above.
+ Maven 2.1 or above.

Currently the NLopt libraries and headers must be installed to c:/nlopt.
The NLopt include files must be under c:/nlopt/include.
The NLopt library files must be under c:/nlopt/lib.

To build follow these steps from a VS 2013 command shell:
```
cmake -G "Visual Studio 12"
```
Above creates VS solution files which can be opened in the IDE. Build the
solution inside the IDE.

To build and test the Java bits, run following from the command shell.
```
mvn test
```

Note that on Windows the required DLLs must be on the PATH.

Mac OS X
--------
Requires following:
+ CMake 2.8.12 or above.
+ GCC 4.2 or above.
+ Java 1.6 or above.
+ Maven 2.1 or above.

Currently the NLopt libraries and headers must be installed to ~/nlopt.
The NLopt include files must be under ~/nlopt/include.
The NLopt library files must be under ~/nlopt/lib.

To build follow these steps:
```
cmake -G "Unix Makefiles"
make
mvn test
```

Reference
=========

The main Java class is `org.nlopt4j.optimizer.NLopt`.

An instance of `NLopt` encapsulates the `NLopt` handle of the native library.

The NLopt constructor is defined as:
```Java
NLopt(int algorithm, int dim)
```
The `algorithm` is an integer constants such as `NLopt.NLOPT_GN_DIRECT`.
The `dim` argument specifies the number of variables in the problem.

The NLopt instance can be destroyed by calling:
```Java
void release()
```
This destroys the underlying native object. I recommend calling this in the
finally block. Note that `release()` will be invoked by the finalizer as well,
calling `release()` multiple times has no effect.

Objective function
------------------
The objective function must implement the following interface.
```Java
class NLopt {
    interface NLopt_func {
        double execute(double[] x, double[] gradient);
    }
}
```
The `x` array contains the values of the variables - the objective
function must return the evaluated result using these parameters.
If the `gradient` array length is equal to the `x` array length, then
the objective function must compute the first order partial derivative
for each `x` and store in the `gradient` array.

If the objective function throws an exception the evaluations will 
be terminated.

Example:
```Java
NLopt.NLopt_func func = new NLopt.NLopt_func() {
    @Override
    public double execute(double[] x, double[] gradient) {
        if (gradient.length == x.length) {
            gradient[0] = 0.0;
            gradient[1] = 0.5 / Math.sqrt(x[1]);
        }
        return Math.sqrt(x[1]);
    }
};
```

Running optimizer
-----------------
The method for running the optimizer has following signature:
```Java
NLoptResult optimize(double[] x)
```

NLoptResult is an object that provides access to the final value 
of the objective function, and the reason for termination.

Methods
-------

Set bounds.
```Java
void setUpperBounds(double[] ub)
void setLowerBounds(double[] lb)
```

Set maximum number of evaluations.
```Java
void setMaxEval(int n)
```

Set stop value.
```Java
void setStopValue(double v)
```

Set maximum time in seconds.
```Java
void setMaxTime(double t)
```

Set objective functions.
```Java
void setMinObjective(NLopt_func func) 
void setMaxObjective(NLopt_func func) 
```

Add equality / inequality constraints.
```Java
void addInequalityConstraint(NLopt_func func, double tol) 
void addEqualityConstraint(NLopt_func func, double tol)
```

Set relative tolerance.
```Java
void setRelativeToleranceOnX(double tol)
```

Get the list of algorithms.
```Java
static String[] getGlobalAlgorithms()
static String[] getLocalAlgorithms() 
```

Convert from algorithm name to integer code.
```Java
static int getAlgorithmCode(String name)
```

Get the text for a return code.
```Java
static String getSuccessDesc(int code)
static String getErrorDesc(int code)
```



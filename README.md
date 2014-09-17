OpenTable Lifecycle Component
=============================

[![Build Status](https://travis-ci.org/opentable/otj-lifecycle.svg)](https://travis-ci.org/opentable/otj-lifecycle)

Component Charter
-----------------

* Control the startup and shutdown of all pieces of code in a larger system.
* Offer defined stages in the lifecycle of a system.
* Offer arbitrary stages and allow easy creation of custom lifecycles.

Usage
-----

Normally, the `Lifecycle` is managed entirely by the server template you use.  But sometimes you
must use a `Lifecycle` in a unit test, or want to customize how the lifecycle runs.

A lifecycle proceeds through a ordered list of
[LifecycleStage](https://github.com/opentable/otj-lifecycle/blob/master/src/main/java/com/opentable/lifecycle/LifecycleStage.java)s
The [DefaultLifecycle](https://github.com/opentable/otj-lifecycle/blob/master/src/main/java/com/opentable/lifecycle/DefaultLifecycle.java)
is `CONFIGURE`, `START`, `STOP`.

[LifecycleListener](https://github.com/opentable/otj-lifecycle/blob/master/src/main/java/com/opentable/lifecycle/LifecycleListener.java)s
may be registered to trigger in each stage.  Listeners are run in the order they are registered during most stages, but run in *reverse* order
during the `STOP` stage.

```java
class MyLifecycledClass {
    private static final Log LOG = Log.findLog();
    public static void main(String[] args) {
        Lifecycle lifecycle = new DefaultLifecycle();
        lifecycle.addListener(LifecycleStage.START_STAGE, () -> { LOG.info("Starting!"); });
        lifecycle.addListener(LifecycleStage.STOP_STAGE,  () -> { LOG.info("Stopping!"); });
        lifecycle.executeTo(LifecycleStage.START_STAGE);
        lifecycle.executeTo(LifecycleStage.STOP_STAGE);
    }
}
```

Guice Integration
-----------------

Objects which are created by Guice ([and specifically only those](https://code.google.com/p/google-guice/wiki/AOP#Limitations),
for example any registered by binding `toInstance` *will not work*) may declare methods to be `@OnStage(LifecycleStage.SOME_STAGE)`.
These methods will be registered and executed exactly as if they were bound with a call to `addListener` immediately after Guice construction
time.

```java
public static class LifecycleTest {
    private static final Log LOG = Log.findLog();
    @OnStage(LifecycleStage.START)
    public void startLifecycle() {
        LOG.info("Starting!");
    }

    @OnStage(LifecycleStage.STOP)
    public void stopLifecycle() {
        LOG.info("Stopping!")
    }
}

@Test
public void testLifecycleAnnotationsOnClass() {
    final Lifecycle lifecycle = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            install (new LifecycleModule());
            bind (LifecycleTest.class).asEagerSingleton();
        }
    }).getInstance(Lifecycle.class);
    lifecycle.executeTo(LifecycleStage.STOP_STAGE);
}
```

Component Level
---------------

*Foundation component*

* Allowed dependencies: logging component.
* Should minimize its dependency footprint.

----
Copyright (C) 2014 OpenTable, Inc.

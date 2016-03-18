package com.appunite.debugutils.debug;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.appunite.detector.SimpleDetector;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func4;
import rx.observers.Observers;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class DebugPresenter {

    private final PublishSubject<Integer> delaySubject = PublishSubject.create();
    private final PublishSubject<SwitchOption> optionSubject = PublishSubject.create();
    private final Observable<Boolean> scalpelObservable;
    private final Observable<List<BaseDebugItem>> scalpelUtilsList;
    private final Observable<Boolean> drawViewsObservable;
    private final Observable<Boolean> showIdObservable;
    private final Observable<List<BaseDebugItem>> utilList;
    private final Observable<Boolean> fpsLabelObservable;
    private final PublishSubject<Integer> actionSubject = PublishSubject.create();
    private final Observable<String> showLogObservable;
    private final Observable<Boolean> leakCanaryObservable;


    @Nonnull
    public Observable<Boolean> setScalpelObservable() {
        return scalpelObservable;
    }

    @Nonnull
    public Observable<Boolean> setDrawViewsObservable() {
        return drawViewsObservable;
    }

    public Observable<Boolean> setShowIdsObservable() {
        return showIdObservable;
    }

    public abstract static class BaseDebugItem implements SimpleDetector.Detectable<BaseDebugItem> {
    }

    public class CategoryItem extends BaseDebugItem {

        @Nonnull
        private final String title;

        public CategoryItem(@Nonnull String title) {
            this.title = title;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        @Override
        public boolean matches(@Nonnull BaseDebugItem item) {
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseDebugItem item) {
            return false;
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                }
            });
        }

    }

    public class InformationItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        @Nonnull
        private final String value;

        public InformationItem(@Nonnull String name, @Nonnull String value) {
            this.name = name;
            this.value = value;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean matches(@Nonnull BaseDebugItem item) {
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseDebugItem item) {
            return equals(item);
        }

        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                }
            });
        }
    }

    public class SpinnerItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        @Nonnull
        private final List<Integer> values;

        public SpinnerItem(@Nonnull String name, @Nonnull List<Integer> values) {
            this.name = name;
            this.values = values;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        @Nonnull
        public List<Integer> getValues() {
            return values;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean matches(@Nonnull BaseDebugItem item) {
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseDebugItem item) {
            return equals(item);
        }

        public Observer<Integer> clickObserver() {
            return Observers.create(new Action1<Integer>() {
                @Override
                public void call(Integer delay) {
                    delaySubject.onNext(delay);
                }
            });
        }
    }

    public class SwitchItem extends BaseDebugItem {

        @Nonnull
        private final String title;
        private int option;

        public SwitchItem(@Nonnull String title, int option) {
            this.title = title;
            this.option = option;
        }

        @Nonnull
        public String getTitle() {
            return title;
        }

        public int getOption() {
            return option;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return title.hashCode();
        }

        @Override
        public boolean matches(@Nonnull BaseDebugItem item) {
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseDebugItem item) {
            return equals(item);
        }

        public Observer<Boolean> switchOption() {
            return Observers.create(new Action1<Boolean>() {
                @Override
                public void call(Boolean set) {
                    optionSubject.onNext(new SwitchOption(set, option));
                }
            });
        }
    }

    public class ActionItem extends BaseDebugItem {

        @Nonnull
        private final String name;
        private int action;

        public ActionItem(@Nonnull String name, int action) {
            this.name = name;
            this.action = action;
        }

        @Nonnull
        public String getName() {
            return name;
        }

        public int getAction() {
            return action;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean matches(@Nonnull BaseDebugItem item) {
            return false;
        }

        @Override
        public boolean same(@Nonnull BaseDebugItem item) {
            return equals(item);
        }

        public Observer<Object> actionOption() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    actionSubject.onNext(action);
                }
            });
        }
    }

    private final BehaviorSubject<List<BaseDebugItem>> simpleListSubject = BehaviorSubject.create();
    private final Observable<List<InformationItem>> deviceInfoList;
    private final Observable<List<InformationItem>> buildInfoList;
    private final PublishSubject<Float> densitySubject = PublishSubject.create();
    private final PublishSubject<String> resolutionSubject = PublishSubject.create();
    private final Context context;

    public DebugPresenter(final Context context) {
        this.context = context;

        deviceInfoList = Observable.combineLatest(
                resolutionSubject,
                densitySubject,
                new Func2<String, Float, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(String resolution, Float density) {
                        return ImmutableList.of(
                                new InformationItem("Model", Build.MANUFACTURER + " " + Build.MODEL),
                                new InformationItem("SDK", DebugDrawerUtils.checkSDKNamme(Build.VERSION.SDK_INT) + "(" + Build.VERSION.SDK_INT + " API)"),
                                new InformationItem("Relase", Build.VERSION.RELEASE),
                                new InformationItem("Resolution", resolution),
                                new InformationItem("Density", Math.round(density) + "dpi"));
                    }
                });

        buildInfoList = densitySubject
                .map(new Func1<Float, List<InformationItem>>() {
                    @Override
                    public List<InformationItem> call(Float density) {
                        return ImmutableList.of(
                                new InformationItem("Name", DebugDrawerUtils.getApplicationName(context)),
                                new InformationItem("Package", context.getPackageName()),
                                new InformationItem("Build Type", DebugDrawerUtils.getBuildType(context)),
                                new InformationItem("Version", DebugDrawerUtils.getBuildVersion(context))
                        );

                    }
                });

        scalpelUtilsList = Observable.just(new Object())
                .map(new Func1<Object, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(Object o) {
                        return ImmutableList.<BaseDebugItem>of(
                                new SwitchItem("Turn Scalpel ", DebugSwitch.SET_SCALPEL),
                                new SwitchItem("Draw Views", DebugSwitch.SCALPEL_DRAW_VIEWS),
                                new SwitchItem("Show Ids", DebugSwitch.SCALPEL_SHOW_ID));
                    }
                });

        utilList = Observable.just(new Object())
                .map(new Func1<Object, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(Object o) {
                        return ImmutableList.<BaseDebugItem>of(
                                new SwitchItem("FPS Label", DebugSwitch.FPS_LABEL),
                                new SwitchItem("LeakCanary", DebugSwitch.LEAK_CANARY),
                                new ActionItem("Show Log", DebugSwitch.SHOW_LOG));
                    }
                });


        Observable.combineLatest(
                deviceInfoList,
                buildInfoList,
                scalpelUtilsList,
                utilList,
                new Func4<List<InformationItem>, List<InformationItem>, List<BaseDebugItem>, List<BaseDebugItem>, List<BaseDebugItem>>() {
                    @Override
                    public List<BaseDebugItem> call(List<InformationItem> deviceInfo, List<InformationItem> buildInfo, List<BaseDebugItem> scalpelUtils, List<BaseDebugItem> utils) {
                        return ImmutableList.<BaseDebugItem>builder()
                                .add(new CategoryItem("Device Information"))
                                .addAll(deviceInfo)
                                .add(new CategoryItem("About app"))
                                .addAll(buildInfo)
                                .add(new CategoryItem("OKHTTP options"))
                                .add(new SpinnerItem("Delay[ms]", ImmutableList.of(100, 200, 300, 500, 1000, 2000, 10000)))
                                .add(new CategoryItem("Scalpel Utils"))
                                .addAll(scalpelUtils)
                                .add(new CategoryItem("Tools"))
                                .addAll(utils)
                                .build();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(simpleListSubject);


        scalpelObservable = optionSubject
               .filter(new Func1<SwitchOption, Boolean>() {
                   @Override
                   public Boolean call(SwitchOption switchOption) {
                       return switchOption.getOption() == DebugSwitch.SET_SCALPEL;
                   }
               })
                .map(checkSet());

        drawViewsObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugSwitch.SCALPEL_DRAW_VIEWS;
                    }
                })
                .map(checkSet());

        showIdObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugSwitch.SCALPEL_SHOW_ID;
                    }
                })
                .map(checkSet());

        fpsLabelObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugSwitch.FPS_LABEL;
                    }
                })
                .map(checkSet());

        leakCanaryObservable = optionSubject
                .filter(new Func1<SwitchOption, Boolean>() {
                    @Override
                    public Boolean call(SwitchOption switchOption) {
                        return switchOption.getOption() == DebugSwitch.LEAK_CANARY;
                    }
                })
                .map(checkSet());

        showLogObservable = actionSubject
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer.equals(DebugSwitch.SHOW_LOG);
                    }
                })
                .map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return "d";
                    }
                });


    }

    @NonNull
    private Func1<SwitchOption, Boolean> checkSet() {
        return new Func1<SwitchOption, Boolean>() {
            @Override
            public Boolean call(SwitchOption switchOption) {
                return switchOption.isSet();
            }
        };
    }


    @Nonnull
    public Observable<List<BaseDebugItem>> simpleListObservable() {
        return simpleListSubject;
    }

    @Nonnull
    public Observer<Float> densityObserver() {
        return densitySubject;
    }

    @Nonnull
    public Observable<Integer> getDelayObservable() {
        return delaySubject;
    }

    @Nonnull
    public Observer<String> resolutionObserver() {
        return resolutionSubject;
    }

    @Nonnull
    public Observable<Boolean> getFpsLabelObservable() {
        return fpsLabelObservable;
    }

    @Nonnull
    public Observable<String> showLogObservable() {
        return showLogObservable;
    }

    @Nonnull
    public Observable<Boolean> getLeakCanaryObservable() {
        return leakCanaryObservable;
    }
}

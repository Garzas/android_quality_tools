package com.appunite.debugutils.debug;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.appunite.debugutils.R;
import com.appunite.detector.ChangesDetector;
import com.appunite.detector.SimpleDetector;
import com.google.common.collect.ImmutableList;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.jakewharton.rxbinding.widget.RxCompoundButton;

import java.util.List;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscription;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

abstract class BaseDebugHolder extends RecyclerView.ViewHolder {

    public BaseDebugHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nonnull DebugPresenter.BaseDebugItem card);

    public abstract void recycle();
}

public class DebugAdapter extends RecyclerView.Adapter<BaseDebugHolder> implements
        Action1<List<DebugPresenter.BaseDebugItem>>, ChangesDetector.ChangesAdapter {

    private static final int TYPE_CATEGORY = 0;
    private static final int TYPE_INFORMATION = 1;
    private static final int TYPE_SWITCH = 2;
    private static final int TYPE_SPINNER = 3;
    private static final int TYPE_ACTION = 4;

    static class CategoryHolder extends BaseDebugHolder {

        @InjectView(R.id.debug_category_title)
        TextView title;

        public CategoryHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            DebugPresenter.CategoryItem categoryItem = (DebugPresenter.CategoryItem) item;
            title.setText(categoryItem.getTitle());

        }

        @Override
        public void recycle() {

        }

        public static CategoryHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new CategoryHolder(inflater.inflate(R.layout.debug_category_item, parent, false));
        }

    }

    static class InformationHolder extends BaseDebugHolder {

        @InjectView(R.id.debug_info_name)
        TextView name;
        @InjectView(R.id.debug_info_value)
        TextView value;

        public InformationHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            DebugPresenter.InformationItem informationItem = (DebugPresenter.InformationItem) item;

            name.setText(informationItem.getName());
            value.setText(informationItem.getValue());
            value.setSelected(true);
        }

        @Override
        public void recycle() {
        }

        public static InformationHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new InformationHolder(inflater.inflate(R.layout.debug_info_item, parent, false));
        }

    }

    static class SpinnerHolder extends BaseDebugHolder {

        private final View view;
        private Subscription mSubscription;

        @InjectView(R.id.debug_spinner_name)
        TextView spinnerName;

        @InjectView(R.id.debug_spinner)
        Spinner spinner;


        public SpinnerHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            DebugPresenter.SpinnerItem spinnerItem = (DebugPresenter.SpinnerItem) item;
            final ArrayAdapter<Integer> adapter = new ArrayAdapter<>(itemView.getContext(), R.layout.debug_spinner_layout, spinnerItem.getValues());

            spinnerName.setText(spinnerItem.getName());
            spinner.setAdapter(adapter);
            mSubscription = new CompositeSubscription(RxAdapterView.itemSelections(spinner)
                    .map(new Func1<Integer, Integer>() {
                        @Override
                        public Integer call(Integer pos) {
                            return adapter.getItem(pos);
                        }
                    })
                    .subscribe(spinnerItem.clickObserver()));

        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static SpinnerHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new SpinnerHolder(inflater.inflate(R.layout.debug_spinner_item, parent, false));
        }

    }

    static class SwitchHolder extends BaseDebugHolder {

        private final View view;
        private DebugDrawerPreferences debugPreferences;
        private Subscription mSubscription;

        @InjectView(R.id.debug_switch)
        Switch debugSwitch;

        @InjectView(R.id.debug_switch_title)
        TextView title;

        public SwitchHolder(View itemView, DebugDrawerPreferences debugPreferences) {
            super(itemView);
            this.view = itemView;
            this.debugPreferences = debugPreferences;
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            final DebugPresenter.SwitchItem switchItem = (DebugPresenter.SwitchItem) item;

            title.setText(switchItem.getTitle());

            mSubscription = new CompositeSubscription(
                    Observable.just(debugPreferences.getLeakCanaryState())
                            .filter(new Func1<Boolean, Boolean>() {
                                @Override
                                public Boolean call(Boolean o) {
                                    return switchItem.getOption() == DebugSwitch.LEAK_CANARY;
                                }
                            })
                            .subscribe(RxCompoundButton.checked(debugSwitch)),
                    RxCompoundButton.checkedChanges(debugSwitch)
                            .skip(1)
                            .subscribe(switchItem.switchOption())
            );


        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static SwitchHolder create(ViewGroup parent, DebugDrawerPreferences debugPreferences) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new SwitchHolder(inflater.inflate(R.layout.debug_switch_item, parent, false), debugPreferences);
        }

    }

    static class ActionHolder extends BaseDebugHolder {

        private final View view;
        private Subscription mSubscription;

        @InjectView(R.id.debug_info_name)
        TextView actionName;

        public ActionHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.inject(this, itemView);
        }

        @Override
        public void bind(@Nonnull DebugPresenter.BaseDebugItem item) {
            DebugPresenter.ActionItem actionItem = (DebugPresenter.ActionItem) item;

            actionName.setText(actionItem.getName());

            mSubscription = new CompositeSubscription(
                    ViewObservable.clicks(view)
                            .subscribe(actionItem.actionOption())
            );


        }

        @Override
        public void recycle() {
            if (mSubscription != null) {
                mSubscription.unsubscribe();
            }
        }

        public static ActionHolder create(ViewGroup parent) {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new ActionHolder(inflater.inflate(R.layout.debug_info_item, parent, false));
        }

    }

//
//    @Nonnull
//    private final Resources mResources;
//    private final InputMethodManager mKeyboard;
//    private final Picasso mPicasso;
//    private final Context mContext;


    @Nonnull
    private final ChangesDetector<DebugPresenter.BaseDebugItem, DebugPresenter.BaseDebugItem> changesDetector;

    @Nonnull
    private List<DebugPresenter.BaseDebugItem> baseDebugItems = ImmutableList.of();

    private DebugDrawerPreferences debugPreferences;


    public DebugAdapter(DebugDrawerPreferences debugPreferences) {
        this.debugPreferences = debugPreferences;
        this.changesDetector = new ChangesDetector<>(new SimpleDetector<DebugPresenter.BaseDebugItem>());
    }


    @Override
    public void call(List<DebugPresenter.BaseDebugItem> baseDebugItems) {
        this.baseDebugItems = baseDebugItems;
        changesDetector.newData(this, baseDebugItems, false);
    }

    @Override
    public BaseDebugHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_CATEGORY) {
            return CategoryHolder.create(parent);
        } else if (viewType == TYPE_INFORMATION) {
            return InformationHolder.create(parent);
        } else if (viewType == TYPE_SWITCH) {
            return SwitchHolder.create(parent, debugPreferences);
        } else if (viewType == TYPE_ACTION) {
            return ActionHolder.create(parent);
        } else if (viewType == TYPE_SPINNER) {
            return SpinnerHolder.create(parent);
        }
        throw new RuntimeException("there is no type that matches the type "
                + viewType
                + " + make sure your using types correctly");
    }


    @Override
    public void onBindViewHolder(BaseDebugHolder holder, int position) {
        holder.bind(baseDebugItems.get(position));
    }


    @Override
    public int getItemViewType(int position) {
        final DebugPresenter.BaseDebugItem item = baseDebugItems.get(position);
        if (item instanceof DebugPresenter.CategoryItem) {
            return TYPE_CATEGORY;
        } else if (item instanceof DebugPresenter.InformationItem) {
            return TYPE_INFORMATION;
        } else if (item instanceof DebugPresenter.SwitchItem) {
            return TYPE_SWITCH;
        } else if (item instanceof DebugPresenter.SpinnerItem) {
            return TYPE_SPINNER;
        } else if (item instanceof DebugPresenter.ActionItem) {
            return TYPE_ACTION;
        } else {
            throw new IllegalStateException("Cannot find item for position" + position);
        }
    }

    @Override
    public int getItemCount() {
        return baseDebugItems.size();
    }

    @Override
    public void onViewRecycled(BaseDebugHolder holder) {
        holder.recycle();
        super.onViewRecycled(holder);
    }
}

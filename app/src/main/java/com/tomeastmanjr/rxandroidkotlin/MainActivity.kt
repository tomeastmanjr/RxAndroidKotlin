package com.tomeastmanjr.rxandroidkotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
    }

    private fun init() {
        initLayout()
    }

    private fun initLayout() {
//        sugar()
        noSugar()
    }

    // To understand how the operator works
    private fun noSugar() {
        // Set up a stream of clicks manually
        // A PublishSubject is like a bridge that can act as both an observable and an observer
        val emitter = PublishSubject.create<View>()
        // attach a standard onClick listener to our button
        button.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View) {
                emitter.onNext(v)
            }
        })

        // create the observer manually
        val observer = object: Observer<View> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: View) {
                incrementCounter2()
            }

            override fun onError(e: Throwable) {
            }
        }

        // RxAndroid also offers a pared down version of Observer
        // this has only one virtual method we can override
        // this is perfect for our needs because we don't care about any of the other lifecycle events in Observer
        // we only care about click events coming down stream
        val consumer = object: Consumer<View> {
            override fun accept(t: View?) {
                incrementCounter2()
            }
        }

        val consumerAsLambda = Consumer<View> { incrementCounter2() }

        emitter
            .map(object: Function<View, View> {
                override fun apply(t: View): View {
                    incrementCounter1()
                    return t
                }
            })
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            //.subscribe(consumer) //pass our hand made observer or consumer to the subscribe function
            //.subscribe(Consumer<View> { incrementCounter2() }) // pass as a lambda directly
            .subscribe { incrementCounter2() } // pass as a lambda directly with removed redundant SAM constructor

    }

    private fun sugar() {
        RxView.clicks(button)
            .map {
                incrementCounter1()
            }
            .throttleFirst(1000, TimeUnit.MILLISECONDS)
            .subscribe {
                incrementCounter2()
            }
    }

    private fun incrementCounter1() {
        var newVal = counter1.text.toString().toInt()
        newVal++
        counter1.text = newVal.toString()

    }

    private fun incrementCounter2() {
        var newVal = counter2.text.toString().toInt()
        newVal++
        counter2.text = newVal.toString()
    }
}

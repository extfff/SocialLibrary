package com.vendor.social.pay;

import android.os.Handler;
import android.os.Message;
import android.os.Process;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步抽像任务，使用方法参考@see ImageDownloader
 * @author skylai
 *
 * @param 传入的参数  抽象方法，子类必须实现
 * @param 更新实时的任务进度 抽象方法，子类必须实现
 * @param 异步方法结束后传回的实体  抽象方法，子类必须实现
 */
abstract class AsyncTaskEx<Params, Progress, Result> {
    private static final String LOG_TAG = "AsyncTaskEx";
    
    /** 线程池维护线程的最少数量 */
    private static final int CORE_POOL_SIZE = 5;
    
    /** 线程池维护线程的最大数量 */
    private static final int MAXIMUM_POOL_SIZE = 10;
    
    /**  线程池维护线程所允许的空闲时间 */
    private static final int KEEP_ALIVE = 10;

    /** 线程池所使用的缓冲队列 */
    private static final LinkedBlockingQueue<Runnable> sWorkQueue = new LinkedBlockingQueue<Runnable>();

    /** 线程池对拒绝任务的处理策略 */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncTaskEx #" + mCount.getAndIncrement());
        }
    };

    /**
     * 线程池中的数量小于CORE_POOL_SIZE，即使线程池中的线程都处于空闲状态，也要创建新的线程来处理被添加的任务。
     * 
     * 线程池中的数量等于 MAXIMUM_POOL_SIZE，但是缓冲队列 workQueue未满，那么任务被放入缓冲队列。
     * 
     * 线程池中的数量大于CORE_POOL_SIZE，缓冲队列sWorkQueue满，并且线程池中的数量小于MAXIMUM_POOL_SIZE，建新的线程来处理被添加的任务。
     * 
     * 线程池中的数量大于CORE_POOL_SIZE，缓冲队列sWorkQueue满，并且线程池中的数量等于MAXIMUM_POOL_SIZE，
     * 那么通过 sThreadFactory所指定的策略来处理此任务。也就是：处理任务的优先级为：核心线程CORE_POOL_SIZE、
     * 任务队列sWorkQueue、最大线程MAXIMUM_POOL_SIZE，如果三者都满了，使用handler处理被拒绝的任务。
     * 
     * 当线程池中的线程数量大于 CORE_POOL_SIZE时，如果某线程空闲时间超过TimeUnit.SECONDS，线程将被终止。
     * 这样，线程池可以动态的调整池中的线程数。
     */
    private static final ThreadPoolExecutor sExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);

    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;
    private static final int MESSAGE_POST_CANCEL = 0x3;

    private static final InternalHandler sHandler = new InternalHandler();

    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;

    private volatile Status mStatus = Status.PENDING;

    public enum Status {

        PENDING,
        RUNNING,
        FINISHED,
    }
        
    public static void clearQueue() {
        sWorkQueue.clear();
    }

    public AsyncTaskEx() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                return doInBackground(mParams);
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @SuppressWarnings("unchecked")
            @Override
            protected void done() {
                Message message;
                Result result = null;

                try {
                    result = get();
                } catch (InterruptedException e) {
                    android.util.Log.w(LOG_TAG, e);
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occured while executing doInBackground()",
                            e.getCause());
                } catch (CancellationException e) {
                    message = sHandler.obtainMessage(MESSAGE_POST_CANCEL,
                            new AsyncTaskExResult<Result>(com.vendor.social.pay.AsyncTaskEx.this, (Result[]) null));
                    message.sendToTarget();
                    return;
                } catch (Throwable t) {
                    throw new RuntimeException("An error occured while executing "
                            + "doInBackground()", t);
                }

                message = sHandler.obtainMessage(MESSAGE_POST_RESULT,
                        new AsyncTaskExResult<Result>(com.vendor.social.pay.AsyncTaskEx.this, result));
                message.sendToTarget();
            }
        };
    }

    public final Status getStatus() {
        return mStatus;
    }

    /** 在onPreExecute 方法执行后马上执行，该方法运行在后台线程中 */
    protected abstract Result doInBackground(Params... params);
    /** 将在执行实际的后台操作前被UI thread调用 */
    protected void onPreExecute(Params... params) {}
    /** 在doInBackground 执行完成后，onPostExecute 方法将被UI thread调用 */
    protected void onPostExecute(Result result) {}
    /** 在publishProgress方法被调用后，UI thread将调用这个方法从而在界面上展示任务的进展情况 */
    protected void onProgressUpdate(Progress... values) {}
    /** 方法取消的时候出发 */
    protected void onCancelled() {}
    
    public final boolean isCancelled() {
        return mFuture.isCancelled();
    }

    public final boolean cancel(boolean mayInterruptIfRunning) {
        return mFuture.cancel(mayInterruptIfRunning);
    }

    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    public final Result get(long timeout, TimeUnit unit) throws InterruptedException,
            ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    @SuppressWarnings("incomplete-switch")
    public final com.vendor.social.pay.AsyncTaskEx<Params, Progress, Result> execute(Params... params) {
        if (mStatus != Status.PENDING) {
            switch (mStatus) {
                case RUNNING:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task is already running.");
                case FINISHED:
                    throw new IllegalStateException("Cannot execute task:"
                            + " the task has already been executed "
                            + "(a task can be executed only once)");
            }
        }

        mStatus = Status.RUNNING;

        onPreExecute(params);

        mWorker.mParams = params;
        sExecutor.execute(mFuture);

        return this;
    }

    protected final void publishProgress(Progress... values) {
        sHandler.obtainMessage(MESSAGE_POST_PROGRESS,
                new AsyncTaskExResult<Progress>(this, values)).sendToTarget();
    }

    private void finish(Result result) {
        onPostExecute(result);
        mStatus = Status.FINISHED;
    }

    private static class InternalHandler extends Handler {
        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskExResult result = (AsyncTaskExResult) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
                case MESSAGE_POST_CANCEL:
                    result.mTask.onCancelled();
                    break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    private static class AsyncTaskExResult<Data> {
        @SuppressWarnings("rawtypes")
        final com.vendor.social.pay.AsyncTaskEx mTask;
        final Data[] mData;

        @SuppressWarnings("rawtypes")
        AsyncTaskExResult(com.vendor.social.pay.AsyncTaskEx task, Data... data) {
            mTask = task;
            mData = data;
        }
    }
}
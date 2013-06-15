package org.powerbot.script.internal.randoms;

import org.powerbot.client.event.PaintListener;
import org.powerbot.script.Script;
import org.powerbot.script.internal.ScriptGroup;
import org.powerbot.script.lang.Stoppable;
import org.powerbot.script.lang.Suspendable;
import org.powerbot.script.lang.Validatable;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.methods.MethodProvider;

import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

public abstract class PollingPassive extends MethodProvider implements Runnable, Suspendable, Stoppable, Validatable, PaintListener {
	public Logger log = Logger.getLogger(getClass().getName());
	private final ScriptGroup container;
	private AtomicBoolean suspended, stopping;

	public PollingPassive(Script script) {
		this(script.getContext(), script.getGroup());
	}

	public PollingPassive(MethodContext ctx, ScriptGroup container) {
		super(ctx);
		this.container = container;
		this.suspended = new AtomicBoolean(false);
		this.stopping = new AtomicBoolean(false);
	}

	public abstract int poll();

	@Override
	public final void run() {
		stopping.set(false);
		while (!isStopping()) {
			int sleep;
			try {
				if (isSuspended()) {
					sleep = 600;
				} else {
					sleep = poll();
				}
			} catch (Throwable e) {
				e.printStackTrace();
				sleep = -1;
			}

			if (sleep > 0) {
				try {
					Thread.sleep(sleep);
				} catch (InterruptedException ignored) {
				}
			} else if (sleep == -1) {
				break;
			}
		}
		stop();
	}

	public ScriptGroup getContainer() {
		return this.container;
	}

	@Override
	public boolean isStopping() {
		return getContainer().isStopping() || stopping.get();
	}

	@Override
	public void stop() {
		if (stopping.compareAndSet(false, true)) {
		}
	}

	@Override
	public void suspend() {
		if (suspended.compareAndSet(false, true)) {
		}
	}

	@Override
	public void resume() {
		if (suspended.compareAndSet(true, false)) {
		}
	}

	@Override
	public final boolean isSuspended() {
		return false;
	}

	@Override
	public void repaint(Graphics render) {
	}
}

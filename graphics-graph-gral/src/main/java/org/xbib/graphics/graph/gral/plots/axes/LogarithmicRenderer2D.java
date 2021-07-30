package org.xbib.graphics.graph.gral.plots.axes;

import java.util.List;

import org.xbib.graphics.graph.gral.plots.axes.Tick.TickType;
import org.xbib.graphics.graph.gral.util.MathUtils;

/**
 * Class that renders axes with a logarithmic scale in two dimensional space.
 */
public class LogarithmicRenderer2D extends AbstractAxisRenderer2D {

	/**
	 * Creates a new renderer for logarithmic scaled axes in two-dimensional
	 * space.
	 */
	public LogarithmicRenderer2D() {
	}

	/**
	 * Converts a world (axis) coordinate value to a view (screen) coordinate
	 * value.
	 * @param axis Axis
	 * @param value World coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @return Screen coordinate value
	 */
	public double worldToView(Axis axis, Number value, boolean extrapolate) {
		checkAxisBounds(axis);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		double val = value.doubleValue();
		if (!extrapolate) {
			if (val <= min) {
				return 0.0;
			}
			if (val >= max) {
				return getShapeLength();
			}
		}
		double minLog = (min > 0.0) ? Math.log10(min) : 0.0;
		double maxLog = (max > 0.0) ? Math.log10(max) : 1.0;
		return (Math.log10(val) - minLog)*getShapeLength() /
			(maxLog - minLog);
	}

	/**
	 * Converts a view (screen) coordinate value to a world (axis) coordinate
	 * value.
	 * @param axis Axis
	 * @param value View coordinate value to convert
	 * @param extrapolate Option to activate extrapolation value that are not
	 *        on the axis
	 * @return World coordinate value
	 */
	public Number viewToWorld(Axis axis, double value, boolean extrapolate) {
		checkAxisBounds(axis);
		double min = axis.getMin().doubleValue();
		double max = axis.getMax().doubleValue();
		if (!extrapolate) {
			if (value <= 0.0) {
				return min;
			}
			if (value >= getShapeLength()) {
				return max;
			}
		}
		double minLog = (min > 0.0) ? Math.log10(min) : 0.0;
		double maxLog = (max > 0.0) ? Math.log10(max) : 1.0;
		return Math.pow(10.0,
				value*(maxLog - minLog)/getShapeLength() + minLog);
	}

	@Override
	public List<Tick> getTicks(Axis axis) {
		checkAxisBounds(axis);
		return super.getTicks(axis);
	}

	@Override
	protected void createTicks(List<Tick> ticks, Axis axis,
                               double min, double max, java.util.Set<Double> tickPositions,
                               boolean isAutoSpacing) {
		double tickSpacing = 1.0;
		if (isAutoSpacing) {
			// TODO Automatic scaling for logarithmic axes
			tickSpacing = 1.0;
		} else {
			tickSpacing = getTickSpacing().doubleValue();
		}

		int ticksMinorCount = getMinorTicksCount();
		double tickSpacingMinor = (ticksMinorCount > 0)
			? tickSpacing/(ticksMinorCount + 1) : tickSpacing;

		// TODO Check if this is a valid solution to allow zeroes
		if (min == 0.0) {
			min = 1.0;
		}

		final double BASE = 10.0;
		double powerMin = MathUtils.magnitude(BASE, min);
		double powerMax = MathUtils.magnitude(BASE, max);
		double minTickMajor = MathUtils.ceil(min, powerMin*tickSpacing);

		int ticksPerPower = (int) Math.floor(BASE/tickSpacingMinor);
		int initialTicksMinor = (int) Math.floor((minTickMajor - min) /
				(powerMin*tickSpacingMinor));

		// Add major ticks
		int i = 0;
		for (double power = powerMin; power <= powerMax; power *= BASE) {
			double multipliedTickSpacingMinor = power*tickSpacingMinor;
			double minTick = MathUtils.ceil(power, multipliedTickSpacingMinor);

			for (int pi = 0; pi < ticksPerPower; pi++) {
				double tickPositionWorld =
					minTick + pi*multipliedTickSpacingMinor;
				if (tickPositionWorld < min) {
					continue;
				} else if (tickPositionWorld > max) {
					break;
				}
				TickType tickType = TickType.MINOR;
				if ((i++ - initialTicksMinor) % (ticksMinorCount + 1) == 0) {
					tickType = TickType.MAJOR;
				}
				Tick tick = getTick(tickType, axis, tickPositionWorld);
				if (tick.position != null
						&& !tickPositions.contains(tickPositionWorld)) {
					ticks.add(tick);
					tickPositions.add(tickPositionWorld);
				}
			}
		}
	}

	/**
	 * Utility method that makes sure that axis bounds comply to rules of
	 * logarithmic axes.
	 * @param axis Axis to be checked
	 */
	private static void checkAxisBounds(Axis axis) {
		if ((axis.getMin().doubleValue() < 0.0)
				|| (axis.getMax().doubleValue() < 0.0)) {
			throw new IllegalStateException(
				"Axis bounds must be greater than or equal to zero for logarithmic axes."); //$NON-NLS-1$
		}
	}

}

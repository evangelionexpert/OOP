package ru.nsu.fit.smolyakov.caclulator.complex;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@code Complex} record obviously represents a complex number, which
 * is defined as an element of a number system that extends the real numbers 
 * with a specific element denoted {@code i}, called the imaginary unit and 
 * satisfying the equation {@code i^2 = -1}; every complex number can be 
 * expressed in the form {@code a+bi}, where {@code a, b ∈ R}. 
 * 
 * <p>This implementation allows to perform some basic algebraical operations,
 * namely {@link #add(Complex)}, {@link #substract(Complex)}, {@link #multiply(Complex)}, 
 * {@link #divide(Complex)} and {@link #negate()}. Such trigonometric functions
 * as {@link #sin()} and {@link #cos()} are also provided.
 * 
 * <p>{@link #toString()} represents an instance of this record as a string 
 * in format {@code a+bi}, where {@code a} is a real part of this complex 
 * number and {@code b} is an imaginary one.
 * 
 * <p>{@code Complex} number can be initialized by the default auto-generated 
 * {@link #Complex(double, double)} constructor or {@link #valueOf(String)}
 * method, which argument has to represent an instance of this record the same 
 * way as it is done by {@link #toString()}.
 */
public record Complex(double r, double i) {
    /**
     * Returns an instance of this record from a string representation 
     * formatted as {@code a+bi}, where {@code a} is a real part of this 
     * complex number and {@code b} is an imaginary one.
     * 
     * <p>Either scientific form ({@code e}-notation) and constants are not supported 
     * for both real and imaginary parts, but integer and decimal floating-point 
     * numbers does.
     * 
     * @param  from  a string-representation of this 
     *               {@code Complex} number
     * @return an instance of {@code Complex} number
     */
    public static Complex valueOf(String from) {
        Pattern regexPattern = 
            Pattern.compile(
                "(?<real>-??\\d+|-??\\d+\\.\\d+)"
                + "(?<imaginarySign>\\+|-)"
                + "(?<imaginary>\\d+|\\d+\\.\\d+)i"
            ); 

        Matcher matcher = regexPattern.matcher(from);
        if (!matcher.matches()) {
            throw new NumberFormatException("complex number have to be presented as A+Bi");
        }

        double r = Double.parseDouble(matcher.group("real"));
        double i = Double.parseDouble(matcher.group("imaginary"));

        if (matcher.group("imaginarySign").equals("-")) {
            i *= -1;
        }

        return new Complex(r, i);
    }

    /**
     * Returns a negation of this complex number.
     * 
     * @return a negation of this complex number
     */
    public Complex negate() {
        return new Complex(-this.r, -this.i);
    }

    /**
     * Returns a result of addition of this complex number and
     * one specified by {@code a}.
     * 
     * @param  a  a specified complex number
     * @return    a result of addition
     */
    public Complex add(Complex a) {
        return new Complex(this.r + a.r, this.i + a.i);
    }

    /**
     * Returns a result of substraction of this complex number and
     * one specified by {@code a}.
     * 
     * @param  a  a specified complex number
     * @return    a result of substraction
     */
    public Complex substract(Complex a) {
        return add(a.negate());
    }

    /**
     * Returns a result of production of this complex number and 
     * one specified by {@code a}.
     * 
     * @param  a  a specified complex number
     * @return    a result of production
     */
    public Complex multiply(Complex a) {
        return new Complex(
            this.r * a.r - this.i * a.i, 
            this.r * a.i + this.i * a.r
        );
    }

    /**
     * Returns a result of dividing this complex 
     * number by one specified by {@code a}.
     * 
     * If {code a} equals {@code 0+0i}, then result
     * is {@code NaN+NaNi}.
     * 
     * @param  a  a specified complex number
     * @return    a result of dividing
     */
    public Complex divide(Complex a) {
        if (a.r == 0 && a.i == 0) {
            return new Complex(Double.NaN, Double.NaN);
        }

        double rTmp = 
            (this.r * a.r + this.i * a.i) 
            / (a.r * a.r + a.i * a.i);
        double iTmp = 
            (a.r * this.i - this.r * a.i) 
            / (a.r * a.r + a.i * a.i);

        return new Complex(rTmp, iTmp);
    }

    /**
     * Returns the trigonometric sine of this complex number.
     *
     * @return  the sine of this complex number
     */
    public Complex sin() {
        return new Complex(
            Math.sin(r) * Math.cosh(i),
            Math.cos(r) * Math.sinh(i)
        );
    }

    /**
     * Returns the trigonometric cosine of this complex number.
     *
     * @return  the cosine of this complex number
     */
    public Complex cos() {
        return new Complex(
            Math.cos(r) * Math.cosh(i),
            - Math.sin(r) * Math.sinh(i)
        );
    }

    /**
     * Returns a string representation of this {@code Complex} number with format 
     * {@code a+bi}, where {@code a} is a real part of this complex 
     * number and {@code b} is an imaginary one.
     * 
     * <p>Both real and imaginary parts might have precision equal to
     * {@code 14} or less.
     * 
     * @return a string representation of this {@code Complex} number 
     */
    @Override
    public String toString() {
        char imaginarySign;
        if (i < 0) {
            imaginarySign = '-';
        } else {
            imaginarySign = '+';
        }

        DecimalFormat decFormatter = new DecimalFormat("0.##############");
        decFormatter.setRoundingMode(RoundingMode.DOWN);

        return "%s%c%si".formatted(
            decFormatter.format(r), 
            imaginarySign, 
            decFormatter.format(Math.abs(i))
        );
    }

    /** 
     * Compares this object against the specified object. The result
     * is {@code true} if and only if the argument is not
     * {@code null} and is a {@code Complex} object that
     * represents a complex number that has both real and 
     * imaginary parts equal to this {@Complex} ones 
     * with 10^-8 precision.
     * 
     * @param o an object to compare to
     * @return  true if this {@code Complex} is equal
     *          to specified {@code o}
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o == this) {
            return true;
        } else if (o instanceof Complex other) {
            var eps = 1e-8;

            return Math.abs(this.i - other.i) < eps
                && Math.abs(this.r - other.r) < eps

                || Double.isNaN(this.i)
                && Double.isNaN(this.r)
                && Double.isNaN(other.i)
                && Double.isNaN(other.r);
        } else {
            return false;
        }
    }
}
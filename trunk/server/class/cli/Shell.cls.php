<?php
/**
 * The `Shell` class is a utility class for shell related information such as
 * width.
 */
class Shell {
	/**
	 * Returns the number of columns the current shell has for display.
	 *
	 * @return int  The number of columns.
	 * @todo Test on more systems.
	 */
	static public function columns() {
		return exec('/usr/bin/env tput cols');
	}

	/**
	 * Checks whether the output of the current script is a TTY or a pipe / redirect
	 *
	 * Returns true if STDOUT output is being redirected to a pipe or a file; false is
	 * output is being sent directly to the terminal.
	 *
	 * @return bool
	 */
	static public function isPiped() {
		return (function_exists('posix_isatty') && !posix_isatty(STDOUT));
	}
}

?>

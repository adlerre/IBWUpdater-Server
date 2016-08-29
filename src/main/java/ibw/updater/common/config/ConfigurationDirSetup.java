/*
 * This program is free software; you can use it, redistribute it
 * and / or modify it under the terms of the GNU General Public License
 * (GPL) as published by the Free Software Foundation; either version 3
 * of the License or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program, in a file called gpl.txt or license.txt.
 * If not, write to the Free Software Foundation Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307 USA
 */
package ibw.updater.common.config;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.status.StatusLogger;

import ibw.updater.common.events.AutoExecutableHandler;
import ibw.updater.common.events.annotation.AutoExecutable;
import ibw.updater.common.events.annotation.Startup;

/**
 * @author Ren\u00E9 Adler (eagle)
 *
 */
@AutoExecutable(name = "Configuration Dir Setup", priority = Integer.MAX_VALUE - 100)
public class ConfigurationDirSetup {

	private static final StatusLogger LOGGER = StatusLogger.getLogger();

	@Startup
	public void startup() {
		loadExternalLibs();
	}

	public static void loadExternalLibs() {
		File resourceDir = ConfigurationDir.getConfigFile("resources");
		if (resourceDir == null) {
			// no configuration dir exists
			return;
		}
		ClassLoader classLoader = ConfigurationDir.class.getClassLoader();
		if (!(classLoader instanceof URLClassLoader)) {
			error(classLoader.getClass() + " is unsupported for adding extending CLASSPATH at runtime.");
			return;
		}
		File libDir = ConfigurationDir.getConfigFile("lib");
		Set<URL> currentCPElements = Stream.of(((URLClassLoader) classLoader).getURLs()).collect(Collectors.toSet());
		Class<? extends ClassLoader> classLoaderClass = classLoader.getClass();
		try {
			Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
			addUrlMethod.setAccessible(true);
			getFileStream(resourceDir, libDir).map(File::toURI).map(u -> {
				try {
					return u.toURL();
				} catch (Exception e) {
					// should never happen for "file://" URIS
					return null;
				}
			}).filter(Objects::nonNull).filter(u -> !currentCPElements.contains(u)).forEach(u -> {
				info("Adding to CLASSPATH: " + u);
				try {
					addUrlMethod.invoke(classLoader, u);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					LOGGER.error("Could not add " + u + " to current classloader.", e);
				}
			});
		} catch (NoSuchMethodException | SecurityException e) {
			LogManager.getLogger().warn(classLoaderClass + " does not support adding additional JARs at runtime", e);
		}
	}

	private static Stream<File> getFileStream(File resourceDir, File libDir) {
		Stream<File> toClassPath = Stream.of(resourceDir);
		if (libDir.isDirectory()) {
			File[] listFiles = libDir
					.listFiles((FilenameFilter) (dir, name) -> name.toLowerCase(Locale.ROOT).endsWith(".jar"));
			if (listFiles.length != 0) {
				toClassPath = Stream.concat(toClassPath, Stream.of(listFiles));
			}
		}
		return toClassPath;
	}

	private static void error(String msg) {
		if (LOGGER.isErrorEnabled()) {
			LOGGER.error(msg);
		} else {
			System.err.println(MessageFormat.format("{0} ERROR\t{1}: {2}", Instant.now().toString(),
					AutoExecutableHandler.class.getSimpleName(), msg));
		}
	}

	private static void info(String msg) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info(msg);
		} else {
			System.out.println(MessageFormat.format("{0} INFO\t{1}: {2}", Instant.now().toString(),
					AutoExecutableHandler.class.getSimpleName(), msg));
		}
	}
}
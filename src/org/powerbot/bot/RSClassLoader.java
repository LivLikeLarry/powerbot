package org.powerbot.bot;

import org.powerbot.client.RandomAccessFile;
import org.powerbot.util.io.IOHelper;

import java.io.File;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permissions;
import java.security.ProtectionDomain;
import java.util.HashMap;
import java.util.Map;

public class RSClassLoader extends ClassLoader {
	private final Map<String, byte[]> classes = new HashMap<>();
	private final ProtectionDomain domain;

	public RSClassLoader(final Map<String, byte[]> classes) {
		this.classes.putAll(classes);

		try {
			final Class<?> raf = RandomAccessFile.class;
			final byte[] data = IOHelper.read(raf.getClassLoader().getResourceAsStream(raf.getName().replace('.', File.separatorChar) + ".class"));
			this.classes.put(raf.getName(), data);
		} catch (final Exception ignored) {
		}

		CodeSource codesource = new CodeSource(null, (java.security.cert.Certificate[]) null);
		Permissions permissions = new Permissions();
		permissions.add(new AllPermission());
		domain = new ProtectionDomain(codesource, permissions);
	}

	@Override
	public final Class<?> loadClass(final String name) throws ClassNotFoundException {
		if (classes.containsKey(name)) {
			final byte buffer[] = classes.remove(name);
			try {
				return defineClass(name, buffer, 0, buffer.length, domain);
			} catch (final Throwable t) {
				t.printStackTrace();
			}
		}
		return super.loadClass(name);
	}
}

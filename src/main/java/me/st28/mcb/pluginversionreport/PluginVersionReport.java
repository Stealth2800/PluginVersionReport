/**
 * PluginVersionReport - Licensed under the MIT License (MIT)
 *
 * Copyright (c) Stealth2800 <http://stealthyone.com/>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.st28.mcb.pluginversionreport;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

public final class PluginVersionReport extends JavaPlugin {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private File location;
    private String fileName;
    private String pluginFormat;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();

        FileConfiguration config = getConfig();

        location = new File(config.getString("location", "plugins"));
        fileName = config.getString("file name", "versions");
        pluginFormat = config.getString("plugin format", "- %1$s (%2$s) : v%3$s");

        generateReport();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("pluginversionreport.create")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this.");
            return true;
        }

        generateReport();
        sender.sendMessage(ChatColor.GREEN + "Report generated.");
        return true;
    }

    public void generateReport() {
        try {
            PrintWriter writer = new PrintWriter(new File(location + File.separator + fileName + ".txt"));

            List<Plugin> plugins = new ArrayList<>();
            Collections.addAll(plugins, Bukkit.getPluginManager().getPlugins());

            Collections.sort(plugins, (o1, o2) -> o1.getName().compareTo(o2.getName()));

            writer.println("PluginVersionReport v" + getDescription().getVersion() + " by Stealth2800 - http://www.stealthyone.com/");
            writer.println();
            writer.println("-----BEGIN REPORT-----");
            writer.println("Report generated: " + DATE_FORMAT.format(new Date()));
            writer.println("Bukkit version: " + Bukkit.getVersion() + " (api: " + Bukkit.getBukkitVersion() + ")");
            writer.println();
            writer.println("Plugins (" + plugins.size() + "):");

            for (Plugin plugin : plugins) {
                writer.println(
                        String.format(
                                pluginFormat,
                                plugin.getName(),
                                plugin.getDescription().getAuthors().toString().replace("[", "").replace("]", ""),
                                plugin.getDescription().getVersion()
                        )
                );
            }

            writer.println("-----END REPORT-----");

            writer.close();
        } catch (Exception ex) {
            getLogger().log(Level.SEVERE, "Unable to generate plugin version report.", ex);
        }
    }

}
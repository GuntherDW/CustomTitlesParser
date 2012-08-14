package be.guntherdw.bukkit;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.*;

/**
 * @author GuntherDW
 */
public class Parser {

    private File path = null;
    public Map<String, Group> groups = new HashMap<String, Group>();
    private Group defaultGroup = null;
    public Map<String, Player> players = new HashMap<String, Player>();

    public Parser(File path) {
        this.path = path;
    }

    public void parse() {
        File pexFile = new File(path, "permissions.yml");
        File userdataFolder = new File(path, "userdata");
        File outputFile = new File(path, "ponycraft_titles.txt");

        /**
         * Parse the PermissionsEx file
         */
        System.out.println("Parsing the PermissionsEx file...");
        this.parsePEXFile(pexFile);

        /**
         * Parse the Essentials UserData files
         */
        System.out.println("Parsing the Essentials userData folder, this might take some time...");
        this.parseEssentialsUserData(userdataFolder);
        System.out.println("Done parsing the Essentials userData folder.");

        System.out.println("Checking for instances in the permissions.yml file, but not in Essentials!");
        this.checkforTypos();

        System.out.println("Writing userData.txt...");
        this.writeOutputFile(outputFile);
        System.out.println("Done.");

    }

    public void checkforTypos() {
        for (Map.Entry<String, Player> entry : this.players.entrySet()) {
            Player p = entry.getValue();
            if (!p.isParsed()) {
                System.out.println("[WARNING] "+p.getUsername()+" has a permissions tag, but no Essentials userdata file!");
            }
        }
    }

    public void writeOutputFile(File outputFile) {
        int done = 0;
        try {

            boolean canWrite = true;

            if (!outputFile.exists())
                canWrite = outputFile.createNewFile();


            if (!canWrite || !outputFile.canWrite()) {
                System.out.println("[SEVERE] Can't write to or create file " + outputFile.getAbsolutePath() + "!");
            } else {
                System.out.println("Going to write "+this.players.size()+" titles to "+outputFile.getAbsolutePath()+"!");
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile));
                for(Map.Entry<String, Player> entry : this.players.entrySet()) {
                    Player p = entry.getValue();
                    String lowercaseName = entry.getKey();
                    if (done % 200 == 0) {
                        System.out.println("Wrote "+done+" titles!");
                        bufferedWriter.flush();
                    }
                    if(p.isParsed()) bufferedWriter.write(lowercaseName+":"+p.prettify()+"\n");
                    done++;
                }

                bufferedWriter.flush();
                bufferedWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getPlayerInfo(String playerName) {
        if (players.containsKey(playerName))
            return players.get(playerName).toString();
        else
            return "[WARNING] Could not find " + playerName + "!";
    }

    public void parseEssentialsUserData(File path) {
        try {
            YamlConfiguration yml = null;
            File[] files = path.listFiles();
            if (files == null) return;

            for (File f : files) {

                String playerName = f.getName().substring(0, f.getName().length() - 4);
                try {
                    // System.out.println("Loading " + playerName + "...");
                    yml = new YamlConfiguration();
                    yml.load(f);
                    Player player = null;
                    if (this.players.containsKey(playerName)) {
                        player = players.get(playerName);
                    } else {
                        player = new Player(playerName);
                    }

                    /**
                     * Safety check!
                     */
                    if (!player.hasGroup()) {
                        player.addGroup(defaultGroup);
                    }

                    String nick = yml.getString("nickname", null);
                    if (nick != null) {
                        player.setNick(nick);
                    }

                    if (!player.hasSuffix()) {
                        Iterator<Group> iterator = player.getGroups().iterator();
                        while (!player.hasSuffix() && iterator.hasNext()) {
                            Group g = iterator.next();
                            if (g == null) {
                                System.out.println("[SEVERE] Tried to fetch non-existent group '" + g + "' for player " + playerName + "!");
                            } else {
                                if (g.hasSuffix()) {
                                    player.setSuffix(g.getSuffix());
                                }
                            }
                        }
                    }

                    if (!player.hasPrefix()) {
                        Iterator<Group> iterator = player.getGroups().iterator();
                        while (!player.hasPrefix() && iterator.hasNext()) {
                            Group g = iterator.next();
                            if (g == null) {
                                System.out.println("[SEVERE] Tried to fetch non-existent group '" + g + "' for player " + playerName + "!");
                            } else {
                                if (g.hasPrefix()) {
                                    player.setPrefix(g.getPrefix());
                                }
                            }
                        }
                    }

                    player.setParsed(true);
                    this.players.put(playerName, player);
                } catch (NullPointerException ex) {
                    System.out.println("NullPointerException on " + playerName + "'s file!");
                    ex.printStackTrace();
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parsePEXFile(File path) {
        try {
            YamlConfiguration yml = new YamlConfiguration();
            yml.load(path);

            /* Parse groups */
            System.out.println("Parsing the groups...");
            ConfigurationSection section = yml.getConfigurationSection("groups");
            if (section == null)
                return;

            Set<String> groups = section.getKeys(false);

            for (String g : groups) {
                Group group = new Group(g);
                group.setPrefix(yml.getString("groups." + g + ".prefix", "").replaceAll("&", "§"));
                group.setSuffix(yml.getString("groups." + g + ".suffix", "").replaceAll("&", "§"));
                List<String> inheritance = yml.getStringList("groups." + g + ".inheritance");
                boolean defaultGroup = yml.getBoolean("groups." + g + ".default");
                if (inheritance.size() != 0) {
                    // System.out.println("Group inheritance : "+inheritance.get(0));
                    group.setInheritance(this.groups.get(inheritance.get(0)));
                }

                group.setDefaultGroup(defaultGroup);
                // System.out.println("Added group " + group);
                if (defaultGroup) {
                    System.out.println(group.getName() + " is the default group!");
                    this.defaultGroup = group;
                }
                this.groups.put(g, group);
            }

            /* Done parsing groups, now onto the players */
            System.out.println("Parsing the players...");
            section = yml.getConfigurationSection("users");
            if (section == null)
                return;

            Set<String> users = section.getKeys(false);
            for (String user : users) {
                Player player = new Player(user);
                player.setPrefix(yml.getString("users." + user + ".prefix", "").replaceAll("&", "§"));
                player.setSuffix(yml.getString("users." + user + ".suffix", "").replaceAll("&", "§"));
                List<String> groupList = yml.getStringList("users." + user + ".group");
                for (String g : groupList) {
                    if (this.groups.containsKey(g)) {
                        player.addGroup(this.groups.get(g));
                    } else {
                        System.out.println("[SEVERE] Player " + user + " is in non-existant group " + g + "!");
                    }
                }
                // System.out.println("Added player "+player);
                this.players.put(user.toLowerCase(), player);
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%LOCAL PATHS%%%%%%%%%%%%%%%%%%%%%
%%% Please specify the following absolute paths %%%
%%%%%% in the contentCleaner_Config.cfg file %%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

-- contentInputDirectory    := the raw data to be processed by the content cleaning routine
-- contentOutputDirectory   := the output directory where the processed files are stored
-- filterDirectoryBase      := the base path of the directory that contains the token-filter files for player/club/trainer/stadium [see 'Folder Structure' section for more info]


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%% FOLDER STRUCTURE %%%%%%%%%%%%%%%%%%
%% Please ensure the following folder structure %%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

-- the path specified for the variable 'filterDirectoryBase' has to contain the following sub-folders
    -- raw
    -- preprocessed

-- the folder 'tokenFilterBase\\raw' has to contain the following files
    -- player.txt
    -- clubs.txt
    -- trainer.txt
    -- stadiums.txt


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%% CONFIGURATION %%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

-- the config-file can be found in: src.Config --> contentCleaner_Config.cfg

-- make sure to select the correct 'OPERATION MODE' in the config-file
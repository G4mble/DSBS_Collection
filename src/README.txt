%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%LOCAL PATHS%%%%%%%%%%%%%%%%%%%%%
%%%% Please specify the following local paths %%%%%
%% in ContentCleaningRoot -> region: Local Paths %%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

-- contentDirectory         := the raw data to be processed by the content cleaning routine
-- saveDirectory            := the output directory where the processed files are stored
-- tokenFilterBase          := the base path of the directory that contains the token-filter files for player/club/trainer [see 'Folder Structure' section for more info]
-- filterOutputDirectory    := the output directory where the processed token-filter-files are stored


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%% FOLDER STRUCTURE %%%%%%%%%%%%%%%%%%
%% Please ensure the following folder structure %%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

-- the path specified for the variable 'tokenFilterBase' has to contain the following sub-folders
    -- raw
    -- preprocessed

-- the folder 'tokenFilterBase\\raw' has to contain the following files
    -- player.txt
    -- clubs.txt
    -- trainer.txt


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%% CONFIGURATION %%%%%%%%%%%%%%%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

-- the config-file can be found in: src.Config --> cleanerConfig.cfg

-- make sure to select the correct 'OPERATION MODE' in the config-file
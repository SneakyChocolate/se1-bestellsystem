#------------------------------------------------------------------------------
# initialize new terminal (bash, zsh/Mac), script is called in settings.json
#------------------------------------------------------------------------------
# 
# probe java is installed
if [[ $(type java &>/dev/null; echo $?) == 0 ]]; then
    # source $HOME/.bashrc or $HOME/.zshrc (Mac)
    [[ "$SHELL" =~ ^.*zsh$ ]] && \
        source ${HOME}/.zshrc || source ${HOME}/.bashrc
    # 
    # on Windows, switch code page to UTF-8 (Windows only)
    [[ $(type chcp.com &>/dev/null; echo $?) == 0 ]] && \
        chcp.com 65001 >/dev/null
    # 
    # source project, if unsoured ('setup()' function exists)
    [[ -z "$(type setup 2>/dev/null | grep function)" ]] && \
        [[ -f .env.sh ]] && source .env.sh
fi

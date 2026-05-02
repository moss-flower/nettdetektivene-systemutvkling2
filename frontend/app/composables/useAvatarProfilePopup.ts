export function useAvatarProfilePopup() {
  const isAvatarProfileOpen = useState('avatar-profile-popup-open', () => false)

  function openAvatarProfile() {
    isAvatarProfileOpen.value = true
  }

  function closeAvatarProfile() {
    isAvatarProfileOpen.value = false
  }

  return {
    isAvatarProfileOpen,
    openAvatarProfile,
    closeAvatarProfile,
  }
}
